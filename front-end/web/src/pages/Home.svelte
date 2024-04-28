<script>
    import Toast from "../components/toast.svelte";
    import { notifications } from "../lib/notification";
    import {loggedIn, accessToken, firstName} from "../stores.js";
    export let appName;

    let emailError = false;
    let authenticationError = false;
    let passwordError = false;
    let loading = false;

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }


    function updateLogIn(token, name) {
        loggedIn.set(true);
        accessToken.set(token);
        firstName.set(name);
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

    async function authenticateAdmin(email, password) {
        let error = false;
        await fetch('http://localhost:8080/api/v1/auth/login', {
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
                updateLogIn(transformed.user.token, transformed.user.firstName);
                window.location.replace('/');
            } 
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
    }


    async function authenticateEmployee () {
        notifications.info("TODO", 1000)
        loading = false;
        return
    }



    function validateLogInDetails(e) {
        clearErrors();
        e.preventDefault();
        let chosenModule = e.target.elements.module.value;
        if (!validateEmail(e.target.elements.email.value)) {
             setEmailError();
             return
        }
        if (e.target.elements.pass.value.length === 0 ) {
             setPasswordError();
             return
        }
        loading = true;
        if (chosenModule === 'admin') {
            authenticateAdmin(e.target.elements.email.value.toLowerCase(), e.target.elements.pass.value);
        } else {
            authenticateEmployee();
        }        
    }
</script>


<div class="main">
    <Toast />
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <a href="/sign-up" id="sign-up">Sign Up</a>
    </div>
    <div class="form">
        <div id="title">
            <h2>Login</h2>
        </div>
        <form on:submit={validateLogInDetails}>
            <label style="font-family: 'Times New Roman', Times, serif; font-size: 1rem;" for="module">Module</label>
            <!--  class="block appearance-none border w-32 border-primary-100"-->
            <select name="module" style=" cursor: pointer; border: 1px #ccc solid; display:block; width: 240px; margin-top: 8px;" id="module">
                <option style="height: 80px;" value="admin">Admin</option>
                <option style="height: 80px;" value="employee">Employee</option>
            </select>

            <label style="display:block; font-family: 'Times New Roman', Times, serif; font-size: 1rem; margin-top: 8px;" for="email">Email</label>
            <input style="border: 1px #ccc solid; display:block; width: 240px; margin-top: 8px;" type="email" name="email" id="email" on:change={clearEmailError}  >
            {#if emailError}
            <p class="text-primary-700 text-base mb-3 font-serif mt-1">Invalid Email Address</p>
            {/if}
            <label style="display:block; font-family: 'Times New Roman', Times, serif; font-size: 1rem; margin-top: 8px;" for="pass">Password</label>
            <input style="border: 1px #ccc solid; display:block; width: 240px; margin-top: 8px;" type="password" name="pass" id="pass" on:change={clearPasswordError}>
            {#if passwordError}
            <p class="text-primary-700 text-base mb-3 font-serif mt-1">Password field cannot be empty</p>
            {/if}
            {#if authenticationError}
                <div style="text-align: center;">
                    <p class="error">Invalid Credentials</p>
                </div>
            {/if}
            <a style="margin-top: 12px; font-family: 'Times New Roman', Times, serif; text-decoration: underline; margin-bottom: 8px;" id="forgot" href="/forgot-password">Forgot Password?</a>
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
    
    #login-btn {
        padding: 12px;
        border-radius: 4px;
        background-color: #5c595c;
        width: 200px;
        border-radius: 20px;
        color:#ffffff;
        margin-top: 8px;
    }
    #login-btn:hover {
        cursor: pointer;
        background-color: #38aa3b;
    }
    h2 {
        font-family: Georgia, 'Times New Roman', Times, serif;
    }

    #forgot:hover {
        cursor: pointer;
        color: #38aa3b;
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