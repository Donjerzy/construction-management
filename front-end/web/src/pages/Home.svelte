<script>
    import Toast from "../components/toast.svelte";
    import { notifications } from "../lib/notification";
    import {loggedIn, accessToken, firstName, projectUUID} from "../stores.js";
    import { Card, Button } from 'flowbite-svelte';
    export let appName;

    let emailError = false;
    let authenticationError = false;
    let passwordError = false;
    let loading = false;
    let projectError = false;
    let projectCode;


    let chosenModule = "admin";

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }


    function updateLogIn(token, name) {
        loggedIn.set(true);
        accessToken.set(token);
        firstName.set(name);
    }

    function updateEmployeeLogIn(token, name, projectId) {
        loggedIn.set(true);
        accessToken.set(token);
        firstName.set(name);
        projectUUID.set(projectId)
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
        notifications.danger("Invalid Credientials", 1000);
    }

    function setProjectError() {
        projectError = true;
    }

    function clearProjectError() {
        projectError = false;
    }

    function clearAuthenticationError() {
        authenticationError = false;
    }

    function clearErrors() {
        clearEmailError();
        clearPasswordError();
        clearAuthenticationError();
        clearProjectError();
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
            if(!response.ok) {
                setAuthenticationError();
                loading = false;
                error = true;
                return
            } else {
                return response.json();
            }
        }).then(transformed=> {
            if(!error) {
                accountType(transformed.user.token, transformed.user.firstName)
            } 
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
    }

    async function accountType(token, firstName) {
        let error = false;
        await fetch('http://localhost:8080/api/v1/employee/member', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
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
                if(transformed.info.count === 0) {
                    updateLogIn(token, firstName);
                    window.location.replace('/');
                } else {
                    switchToEmployeeModule(transformed.info.project, token)
                }
            } 
        }).catch(error=> {
            loading = false;
            console.log(error);
            notifications.danger("Could make request to server", 1000)
        })
        return
    }


    // async function authenticateEmployee (project, email, password) {
    //     let error = false;
    //     await fetch('http://localhost:8080/api/v1/employee/auth/login', {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify( {
    //             project: project,
    //             email: email,
    //             password: password
    //         })
    //     }).then(response=> {
    //         loading = false;
    //         if(!response.ok) {
    //             setAuthenticationError();
    //             error = true;
    //             return
    //         } else {
    //             return response.json();
    //         }
    //     }).then(transformed=> {
    //         if(!error) {
    //             updateEmployeeLogIn(transformed.user.token, transformed.user.firstName, transformed.user.project);
    //             window.location.replace('/employee/home');
    //         } 
    //     }).catch(error=> {
    //         loading = false;
    //         console.log(error);
    //         notifications.danger("Could make request to server", 1000)
    //     })
    //     return
    // }



    function validateLogInDetails(e) {
        clearErrors();
        e.preventDefault();
        // let chosenModule = e.target.elements.module.value;
        if (!validateEmail(e.target.elements.email.value)) {
             setEmailError();
             return
        }
        if (e.target.elements.pass.value.length === 0 ) {
             setPasswordError();
             return
        }
        // if(chosenModule === "employee" && projectCode === undefined) {
        //     setProjectError();
        //     return
        // }
        // if(chosenModule === "employee" && projectCode.length === 0) {
        //     setProjectError();
        //     return
        // }
        loading = true;
        // if (chosenModule === 'admin') {
        //     authenticateAdmin(e.target.elements.email.value.toLowerCase(), e.target.elements.pass.value);
        // } else {
        //     authenticateEmployee(projectCode, e.target.elements.email.value.toLowerCase(), e.target.elements.pass.value);
        // }       
        authenticateAdmin(e.target.elements.email.value.toLowerCase(), e.target.elements.pass.value);
    }

    async function switchToEmployeeModule(chosenProject, accessToken) {
        if (chosenProject === undefined || chosenProject === "0") {
            return notifications.danger("Project not chosen", 1000);
        }
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/employee/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                    project: chosenProject,
                    token: accessToken,
                    from: "admin"
            })
        }).then(response=> {
            loading = false;
            if(response.status === 400) {
                existsError = true;
                return
            }
            if(!response.ok) {
                error = true;
                return
            } else {
                error = false;
                existsError = false;
                return response.json();
            }
        }).then(transformed=> {
            if(!error) {
                updateEmployeeLogIn(transformed.user.token, transformed.user.firstName, transformed.user.project);
                window.location.replace('/employee/home');
            } 
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000);
        })
        if(error) {
            firstName.set("");
            accessToken.set("");
            loggedIn.set("false");
            window.location.replace('/'); 
        }
    }
</script>


<svelte:head>
    <title>Log In</title>
</svelte:head>


<div class="main">
    <Toast />
    <div id="header">
        <div style="display: flex; align-items: center; gap: 8px;">
            <svg class="fill-black h-7 w-7"  xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M18 15.5C18.83 15.5 19.5 16.17 19.5 17S18.83 18.5 18 18.5 16.5 17.83 16.5 17 17.17 15.5 18 15.5M18 14C16.34 14 15 15.34 15 17S16.34 20 18 20C19.66 20 21 18.66 21 17S19.66 14 18 14M7 8L5 5H2V7H5.13L8 11.3V20H10L12.57 15H14.55C15.25 13.81 16.5 13 18 13C18.88 13 19.69 13.29 20.35 13.78L22 8H7M9.39 19H9V12.8L9.87 14.11C10.23 14.64 10.82 14.96 11.45 15L9.39 19Z" /></svg>
            <h1 class="app-name">{appName} Construction App</h1>
        </div>
        <a href="/sign-up" id="sign-up">Sign Up</a>
    </div>
    <div class="form">
        <Card>
            <div id="title">
                <h2>Login</h2>
            </div>
            <form on:submit={validateLogInDetails}>
                <!-- <label style="font-family: 'Times New Roman', Times, serif; font-size: 1rem;" for="module">Module</label> -->
                <!--  class="block appearance-none border w-32 border-primary-100"-->
                <!-- <select bind:value={chosenModule} name="module" style="font-family: sans-serif; cursor: pointer; border: 1px #ccc solid; display:block; width: 240px; margin-top: 8px;" id="module">
                    <option style="height: 80px;" value="admin">Admin</option>
                    <option style="height: 80px;" value="employee">Employee</option>
                </select> -->

                <!-- {#if chosenModule === "employee"}
                    <label for="project_id" style="display:block; font-family: 'Times New Roman', Times, serif; font-size: 1rem; margin-top: 8px;">Project</label>
                    <input bind:value={projectCode} name="project_id" style="font-family: sans-serif; border: 1px #ccc solid; display:block; width: 240px; margin-top: 8px;" type="text" id="project_id" on:change={clearProjectError}>
                    {#if projectError}
                        <p class="text-primary-700 text-base mb-3 font-serif mt-1">Invalid Project</p>
                    {/if}
                {/if} -->

                <label style="display:block; font-family: 'Times New Roman', Times, serif; font-size: 1rem; margin-top: 8px;" for="email">Email</label>
                <input style="font-family: sans-serif; border: 1px #ccc solid; border-radius: 4px; display:block; width: 100%; margin-top: 8px;" type="email" name="email" id="email" on:change={clearEmailError}  >
                {#if emailError}
                <p class="text-primary-700 text-base mb-3 font-serif mt-1">Invalid Email Address</p>
                {/if}
                <label style="display:block; font-family: 'Times New Roman', Times, serif; font-size: 1rem; margin-top: 8px;" for="pass">Password</label>
                <input style="font-family: sans-serif; border: 1px #ccc solid; border-radius: 4px; display:block; width: 100%; margin-top: 8px;" type="password" name="pass" id="pass" on:change={clearPasswordError}>
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
                    <div style="display: flex; justify-content: center;">
                        <Button type="submit">Log in</Button>
                        <!-- <button type="submit" id="login-btn">Log in</button> -->
                    </div>
                {/if}
            </form>
        </Card>
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
        font-family: sans-serif;
    }
    #sign-up:hover {
        color: #38aa3b;
        cursor: pointer;
        text-decoration: underline;
    }

    .main {
        /* border: solid 1px black; */
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
        /* border: solid 1px black */
    }
    #title {
        margin-bottom: 20px;
        font-size: 1.4rem;
    }
    /* #login-btn {
        padding: 12px;
        border-radius: 4px;
        background-color: #5c595c;
        width: 200px;
        border-radius: 20px;
        color:#ffffff;
        margin-top: 8px;
        font-family: sans-serif;
    }
    #login-btn:hover {
        cursor: pointer;
        background-color: #38aa3b;
    } */
    h2 {
        font-family: Georgia, 'Times New Roman', Times, serif;
    }

    #forgot {
        font-family: 'Times New Roman', Times, serif;
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
        margin-top: 8px;
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