<script>
    import { notifications } from '../lib/notification.js'
    import Toast from '../components/toast.svelte'
    export let appName;
    // stages = mail, enter-verification-code, change-password
    let stage = 'mail';
    let emailError = false;
    let loading = false;
    let verificationCode = undefined;
    let verificationCodeError = false;
    let userEmail = undefined;
    let firstNameError = false;
    let lastNameError = false;
    let passwordError = false;
    let passwordErrorMessage = '';
    let matchingError = false;

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    async function sendVerificationEmail(email) {
        const res = await fetch(`http://localhost:8080/api/v1/auth/forgot-password?email=${email}`);
        const json = await res.json();
        loading = false;
        if (json.httpStatus === 200) {
            userEmail = email;
            stage = 'enter-verification-code';
        } else {
            setEmailError();
        }  
    }


    async function changePassword(password) {
        let errorFetch = false;
        const res = await fetch(`http://localhost:8080/api/v1/auth/new-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: userEmail,
                password: password,
                code: verificationCode,
                
            })
        }).then(res=>{
            if(!res.ok) {
                loading = false;
                errorFetch = true;
                return
            } else {
                loading = false;
            }
        }).catch(e => {
            loading = false;
            notifications.danger('Could not change Password', 2000);
            errorFetch = true;
            return
        });
        if(errorFetch) {
            notifications.danger('Could not change Password', 2000)
            return;
        } else {
            notifications.success('Password Changed Successfully', 2000);
            setTimeout(()=> {
                return window.location.replace('/')
            },1500)
            return;
        }     
    }


    async function resendVerificationCode() {
        loading = true;
        const res = await fetch(`http://localhost:8080/api/v1/auth/forgot-password?email=${userEmail}`);
        const json = await res.json();
        loading = false;
        if (json.httpStatus === 200) {
            notifications.success('Verification Code has been re-sent successfully', 2000)
        }
    }


    async function verifyUserCode(email, code) {
        const res = await fetch(`http://localhost:8080/api/v1/auth/validate-code?email=${email}&code=${code}`);
        const json = await res.json();
        loading = false;
        if (json.httpStatus === 200) {
            stage = 'change-password';
            verificationCode = json.value
        } else {
            setVerificationCodeError();
        }  
    }


    function handleEmailSubmit(e) {
        clearEmailError();
        e.preventDefault();
        const email = e.target.elements.email.value;
        if (validateEmail(email)) {
            loading = true;
            sendVerificationEmail(email.toLowerCase());
        } else {
            setEmailError();
        }
    }

    function clearEmailError() {
        emailError = false;
    }

    function clearVerificationCodeError() {
        verificationCodeError = false;
    }

    function setEmailError() {
        emailError = true;
    }

    function setPasswordError(text) {
        passwordError = true;
        passwordErrorMessage = text;
    }

    function setMatchingError() {
        matchingError = true;
    }

    function clearMatchingError() {
        matchingError = false;
    }

    function clearPasswordError() {
        passwordError = false;
        passwordErrorMessage = '';
    }

    function setVerificationCodeError() {
        verificationCodeError = true;
    }

    function handleCodeSubmit(e) {
        clearVerificationCodeError();
        e.preventDefault();
        const num = parseInt(`${e.target.elements.num1.value}${e.target.elements.num2.value}${e.target.elements.num3.value}${e.target.elements.num4.value}`);
        if (isNaN(num)) {
            setVerificationCodeError();
            return
        }
        loading=true;
        verifyUserCode(userEmail, num);
    }

    function clearSignUpErrors() {
        clearPasswordError();
        clearMatchingError();
    }

    function handleDetailsSubmit(e) {
        e.preventDefault();
        clearSignUpErrors();
        let hasNoUpper = true;
        let hasNoLower = true;
        let hasNoDigit = true;
        if(e.target.elements.pass.value.length < 6) {
            setPasswordError('Password should have atleast 6 characters');
            return;
        }
        for (let i=0; i < e.target.elements.pass.value.length; i++) {
            if (/[A-Z]/.test(e.target.elements.pass.value[i])) {
                hasNoUpper = false;
            } else if (/[a-z]/.test(e.target.elements.pass.value[i])) {
                hasNoLower = false;
            } else if (/\d/.test(e.target.elements.pass.value[i])) {
                hasNoDigit = false;
            }
            if (hasNoLower === false && hasNoUpper === false && hasNoDigit === false) {
                break;
            }
        }
        if (hasNoUpper) {
            setPasswordError('Password should have atleast 1 uppercase character'); 
            return;
        }
        if (hasNoLower) {
            setPasswordError('Password should have atleast 1 lowercase character'); 
            return;
        }
        if (hasNoDigit) {
            setPasswordError('Password should have atleast 1 digit'); 
            return;
        }
        if (e.target.elements.pass.value !== e.target.elements.cpass.value) {
            setMatchingError();
            return;
        }
        loading = true;
        changePassword(e.target.elements.pass.value);
    }

</script>


<svelte:head>
    <title>Sign Up</title>
</svelte:head>


{#if stage === 'mail'}
<div class="main">
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <div id="title">
            <h2>Forgot Password</h2>
        </div>
        <form on:submit={handleEmailSubmit}>
            <label for="email">Email</label>
            <input type="email" name="email" id="email" on:change={clearEmailError}>
            {#if emailError}
            <p class="error">Invalid Email Address</p>
            {/if}
            {#if !loading}
            <button type="submit" id="login-btn">Next</button>
            {:else}
            <div id="loader-div">
                <span class="loader"></span>
            </div>
            {/if}  
        </form>
    </div>
</div>
{:else if stage === 'enter-verification-code'}
<div class="main">
    <Toast />
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <div id="title">
            <h2>Forgot Password</h2>
        </div>
        <form on:submit={handleCodeSubmit} on:change={clearVerificationCodeError}>
            <div id="verif-code-label-div">
                <label for="verif-code">Verification Code</label>
            </div>
            <div id="verification-code-div">
                <input name="num1" type="text" maxlength="1" class="verif-input" />
                <input name="num2" type="text" maxlength="1" class="verif-input" />
                <input name="num3" type="text" maxlength="1" class="verif-input"/>
                <input name="num4" type="text"maxlength="1" class="verif-input"/>
            </div>
            {#if verificationCodeError}
            <div class="center-text-div">
                <p class="error">Invalid Verification Code</p>
             </div>
            {/if}
                <div class="center-text-div">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p on:click={resendVerificationCode} id="forgot">Re-send Verification Code</p>
                </div>
            {#if !loading}
                <button type="submit" id="login-btn">Next</button>
            {:else}
                <div id="loader-div">
                    <span class="loader"></span>
                </div>
            {/if}  
        </form>
    </div>
</div>
{:else if stage === 'change-password'}
<div class="main">
    <Toast />
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <div id="title">
            <h2>Forgot Password</h2>
        </div>
        <form on:submit={handleDetailsSubmit}>
            <label for="pass">New Password</label>
            <input type="password" name="pass" id="pass" on:change={clearPasswordError}>
            {#if passwordError}
            <p class="error">{passwordErrorMessage}</p>
            {/if}
            <label for="cpass">Confirm New Password</label>
            <input type="password" name="cpass" id="cpass" on:change={clearMatchingError}>
            {#if matchingError}
            <p class="error">The passwords do not match</p>
            {/if}
            {#if !loading}
            <button type="submit" id="login-btn">Sign Up</button>
            {:else}
            <div id="loader-div">
                <span class="loader"></span>
            </div>
            {/if}  
        </form>
    </div>
</div>
{/if}





<style>
    * {
        margin: 0;
        border: 0;
        /* box-sizing: border-box; */
    }
    a {
        text-decoration: none;
    }
    #header {
        display: flex;
        justify-content: space-between;
    }
    #log-in {
        font-size: 1.4rem;
    }
    #log-in:hover {
        color: #38aa3b;
        cursor: pointer;
        text-decoration: underline;
    }

    .center-text-div {
        text-align: center;
    }

    .error {
        color: red;
        font-size: 1rem;
        font-family: 'Times New Roman', Times, serif;
        margin-bottom: 12px;
    }

    .main {
        position: relative;
        margin-left: 12%;
        margin-right: 12%;
        max-width: 100vw;
        min-height: 90vh;
        max-height: 90vh;
        padding: 20px;
        /* border: solid black 1px; */
    }
    .app-name {
        font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
        font-size: 1.5rem;
    }
    .app-name:hover {
        cursor: pointer;
    }
    .form {
        height: 80vh;
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }
    #title {
        margin-bottom: 20px;
        font-size: 1.4rem;
    }
    input {
        display: block;
        border: 1px solid;
        margin-bottom: 20px;
        height: 20px;
        width: 184px;
        padding: 4px;
        border-color: #ccc;
        margin-top: 4px;
    }
    #verif-code-label-div{
        display: flex;
        justify-content: center;
    }
    #verification-code-div{
        display: flex;
        gap: 4px;
        justify-content: center;
    }
    #forgot {
        margin-bottom: 12px;
        font-family: 'Times New Roman', Times, serif;
        color: #434143;
        font-size: 1rem;
    }
    #forgot:hover {
        cursor: pointer;
        color: #38aa3b;
        text-decoration: underline;
    }
    .verif-input {
        border: 1px solid;
        margin-bottom: 20px;
        height: 20px;
        width: 20px;
        padding: 4px;
        padding-left: 8px;
    }
    #login-btn {
        padding: 12px;
        border-radius: 4px;
        background-color: #5c595c;
        width: 200px;
        border-radius: 20px;
        color:#ffffff;
    }
    #login-btn:hover {
        cursor: pointer;
        background-color: #38aa3b;
    }
    h2 {
        font-family: Georgia, 'Times New Roman', Times, serif;
    }
    label {
        font-family: 'Times New Roman', Times, serif;
        font-size: 1rem;
    }
    #loader-div {
        display: flex;
        justify-content: center;
    }  
    .loader {
        width: 48px;
        height: 48px;
        display: inline-block;
        position: relative;
    }
    .loader::after,
    .loader::before {
        content: '';  
        box-sizing: border-box;
        width: 48px;
        height: 48px;
        border-radius: 50%;
        border: 2px solid black;
        position: absolute;
        left: 0;
        top: 0;
        animation: animloader 2s linear infinite;
    }
    .loader::after {
        animation-delay: 1s;
    }

    @keyframes animloader {
        0% {
            transform: scale(0);
            opacity: 1;
        }
        100% {
            transform: scale(1);
            opacity: 0;
        }
    }  
    
</style>