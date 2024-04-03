<script>
    import { notifications } from "../lib/notification";
    import {loggedIn, accessToken} from "../stores.js";

    export let appName;

    let emailError = false;
    let authenticationError = false;
    let passwordError = false;
    let loading = false;

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }


    function updateLogIn(token) {
        loggedIn.set(true);
        accessToken.set(token);
    }

    function setEmailError() {
        emailError = true;
    }

    function clearEmailError() {
        emailError = false;
        authenticationError = false;
    }

    function setPasswordError() {
        passwordError = true;
    }

    function clearPasswordError() {
        passwordError = false;
        authenticationError = false;
    }

    function setAuthenticationError() {
        authenticationError = true;
    }

    function clearAuthenticationError() {
        authenticationError = false;
    }

    function clearErrors() {
        clearEmailError();
        clearPasswordError();
        clearAuthenticationError();
    }

    async function authenticate(email, password) {
        let error = false;
        const res = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify( {
                email: email,
                password: password
            })
        }).then(response=> {
            loading = false;
            if(!response.ok) {
                setAuthenticationError();
                error = true;
                return
            } else {
                return response.json();
            }
        }).then(transformed=> {
            if(!error) {
                updateLogIn(transformed.value);
                window.location.replace('/');
            } 
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
    }


    function validateLogInDetails(e) {
        clearErrors();
        e.preventDefault();
        if (!validateEmail(e.target.elements.email.value)) {
             setEmailError();
             return
        }
        if (e.target.elements.pass.value.length === 0 ) {
             setPasswordError();
             return
        }
        loading = true;
        authenticate(e.target.elements.email.value.toLowerCase(), e.target.elements.pass.value);
    }



</script>

<div class="main">
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <a href="/sign-up" id="sign-up">Sign Up</a>
    </div>
    <div class="form">
        <div id="title">
            <h2>Login</h2>
        </div>
        <form on:submit={validateLogInDetails}>
            <label for="email">Email</label>
            <input type="email" name="email" id="email" on:change={clearEmailError}  >
            {#if emailError}
            <p class="error">Invalid Email Address</p>
            {/if}
            <label for="pass">Password</label>
            <input type="password" name="pass" id="pass" on:change={clearPasswordError}>
            {#if passwordError}
            <p class="error">Password field cannot be empty</p>
            {/if}
            {#if authenticationError}
                <div style="text-align: center;">
                    <p class="error">Invalid Credentials</p>
                </div>
            {/if}
            <a id="forgot" href="/forgot-password">Forgot Password?</a>
            {#if loading}
                <div id="loader-div">
                    <span class="loader"></span>
                </div>
            {:else}
                <button type="submit" id="login-btn">Log in</button>
            {/if}
        </form>
    </div>
</div>


<style>
    * {
        margin: 0;
        border: 0;
        /* box-sizing: border-box; */
    }
    a {
        text-decoration: none;
        display: block;
    }
    #header {
        display: flex;
        justify-content: space-between;
    }
    #sign-up {
        font-size: 1.4rem;
    }
    #sign-up:hover {
        color: #38aa3b;
        cursor: pointer;
        text-decoration: underline;
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
    h2 {
        font-family: Georgia, 'Times New Roman', Times, serif;
    }
    label {
        font-family: 'Times New Roman', Times, serif;
        font-size: 1rem;
    }
    .error {
        color: red;
        font-size: 1rem;
        font-family: 'Times New Roman', Times, serif;
        margin-bottom: 12px;
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