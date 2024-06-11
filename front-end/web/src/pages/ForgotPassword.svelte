<script>
    import { notifications } from '../lib/notification.js'
    import Toast from '../components/toast.svelte'
    export let appName;
    import { Card, Button } from 'flowbite-svelte';
    // stages = mail, enter-verification-code, change-password
    let stage = 'mail';
    // let stage = 'mail';
    // let stage = 'enter-verification-code';
    // let stage = 'change-password';
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
        <div style="display: flex; align-items: center; gap: 8px;">
            <svg class="fill-black h-7 w-7"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <h1 class="app-name">{appName} Construction App</h1>
        </div>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <Card>
            <div id="title">
                <h2>Forgot Password</h2>
            </div>
            <form on:submit={handleEmailSubmit}>
                <label for="email">Email</label>
                <input type="email" style="width: 100%; border-radius: 4px;" name="email" id="email" on:change={clearEmailError}>
                {#if emailError}
                <p class="error">Invalid Email Address</p>
                {/if}
                {#if !loading}
                <div style="display: flex; justify-content: center; align-items: center;">
                    <Button type="submit">Next</Button>
                </div>
                {:else}
                <div id="loader-div">
                    <span class="loader"></span>
                </div>
                {/if}  
            </form>
        </Card>
    </div>
</div>
{:else if stage === 'enter-verification-code'}
<div class="main">
    <Toast />
    <div id="header">
        <div style="display: flex; align-items: center; gap: 8px;">
            <svg class="fill-black h-7 w-7"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <h1 class="app-name">{appName} Construction App</h1>
        </div>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <Card>
            <div id="title">
                <h2>Forgot Password</h2>
            </div>
            <form on:submit={handleCodeSubmit} on:change={clearVerificationCodeError}>
                <div id="verif-code-label-div">
                    <label for="verif-code" style="font-family: 'Times New Roman', Times, serif; font-size: 1rem;">Verification Code</label>
                </div>
                <div id="verification-code-div">
                    <input style="border: 1px #ccc solid; height: 32px; width:32px; display:block; margin-top: 8px;" name="num1" type="text" maxlength="1" class="verif-input" />
                    <input style="border: 1px #ccc solid; height: 32px; width:32px; display:block; margin-top: 8px;" name="num2" type="text" maxlength="1" class="verif-input" />
                    <input style="border: 1px #ccc solid; height: 32px; width:32px; display:block; margin-top: 8px;" name="num3" type="text" maxlength="1" class="verif-input"/>
                    <input style="border: 1px #ccc solid; height: 32px; width:32px; display:block; margin-top: 8px;" name="num4" type="text"maxlength="1" class="verif-input"/>
                </div>
                {#if verificationCodeError}
                <div class="center-text-div" style="display: flex; justify-content: center;">
                    <p class="error">Invalid Verification Code</p>
                 </div>
                {/if}
                    <div class="center-text-div" style="display: flex; justify-content: center;">
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                        <p style="margin-top: 4px; font-family: 'Times New Roman', Times, serif; text-decoration: underline; margin-bottom: 8px;" on:click={resendVerificationCode} id="forgot">Re-send Verification Code</p>
                    </div>
                {#if !loading}
                <div style="display: flex; justify-content: center; align-items: center;">
                    <Button type="Submit">Next</Button>
                </div>
                {:else}
                    <div id="loader-div">
                        <span class="loader"></span>
                    </div>
                {/if}  
            </form>
        </Card>
    </div>
</div>
{:else if stage === 'change-password'}
<div class="main">
    <Toast />
    <div id="header">
        <div style="display: flex; align-items: center; gap: 8px;">
            <svg class="fill-black h-7 w-7"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <h1 class="app-name">{appName} Construction App</h1>
        </div>
        <a href="/" id="log-in">Log In</a>
    </div>
    <div class="form">
        <Card>
            <div id="title">
                <h2>Forgot Password</h2>
            </div>
            <form on:submit={handleDetailsSubmit}>
                <label for="pass">New Password</label>
                <input type="password" style="width: 100%; border-radius: 4px;" name="pass" id="pass" on:change={clearPasswordError}>
                {#if passwordError}
                <p class="error">{passwordErrorMessage}</p>
                {/if}
                <label for="cpass">Confirm New Password</label>
                <input type="password" style="width: 100%; border-radius: 4px;" name="cpass" id="cpass" on:change={clearMatchingError}>
                {#if matchingError}
                <p class="error">The passwords do not match</p>
                {/if}
                {#if !loading}
                <div style="display: flex; justify-content: center; align-items: center;">
                    <Button type="submit">Reset Password</Button>
                </div>

                {:else}
                <div id="loader-div">
                    <span class="loader"></span>
                </div>
                {/if}  
            </form>
        </Card>
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
        font-family: 'Times New Roman', Times, serif;
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
        font-family: 'Times New Roman', Times, serif;
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
        font-family: 'Times New Roman', Times, serif;
    }
    input {
        display: block;
        margin-bottom: 20px;
        padding: 4px;
        border-color: #ccc;
        margin-top: 4px;
        font-family: sans-serif; 
        border: 1px #ccc solid; 
        width: 240px; 
        margin-top: 8px;
        height: 40px;
    }
    #verif-code-label-div{
        display: flex;
        justify-content: center;
    }
    #verification-code-div{
        display: flex;
        gap: 8px;
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
        height: 28px;
        width: 28px;
        padding: 4px;
        padding-left: 8px;
        border-radius: 4px;
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