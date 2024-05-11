<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import { onMount } from "svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import Loader from "../components/loading-component.svelte";
    import {page} from '$app/stores';
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Reset Password";
    let loading = false;
    let newPassword = "";
    let employeeId = $page.params.employeeId;
    
    
    async function resetPassword() {
        let hasNoDigit = true;
        let hasNoLower = true;
        let hasNoUpper = true;
        if(newPassword.length < 6) {
            notifications.danger('Password should have atleast 6 characters', 1000);
            return;
        }
        for (let i=0; i < newPassword.length; i++) {
            if (/[A-Z]/.test(newPassword[i])) {
                hasNoUpper = false;
            } else if (/[a-z]/.test(newPassword[i])) {
                hasNoLower = false;
            } else if (/\d/.test(newPassword[i])) {
                hasNoDigit = false;
            }
            if (hasNoLower === false && hasNoUpper === false && hasNoDigit === false) {
                break;
            }
        }
        if (hasNoUpper) {
            notifications.danger('Password should have atleast 1 uppercase character', 1000);
            return;
        }
        if (hasNoLower) {
            notifications.danger('Password should have atleast 1 lowercase character', 1000);
            return;
        }
        if (hasNoDigit) {
            notifications.danger('Password should have atleast 1 digit', 1000);
            return;
        }

        loading = true;
        let error = false;
        let existsError = false;
        let errorMessage;
        await fetch('http://localhost:8080/api/v1/employee/modify-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify({
                employeeId: employeeId,
                newPassword: newPassword
            })
        }).then(response=> {
            loading = false;
            if(response.status === 400) {
                existsError = true;
                return response.json();
            }
            if(!response.ok) {
                error = true;
                return
            } else {
                error = false;
                existsError = false;
                notifications.success("Password reset successfully", 1000);
                window.history.back();
                return;
            }
        }).then((res)=> {
            if(existsError) {
                errorMessage = res.message
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger(errorMessage, 1000); 
        }
        if(error) {
            firstName.set("");
            accessToken.set("");
            loggedIn.set("false");
            window.location.replace('/'); 
        }
    }
</script>

<svelte:head>
    <title>Reset Password</title>
</svelte:head>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <Toast />
    	<div class="mt-8">
            <label for="password" class="block text-base">New Password</label>
            <input class="mt-3 w-full" type="password" id="password" bind:value={newPassword}>
            <div class="mt-4 flex justify-center">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                    height=12 width=36 label="Reset Password" fontSize="sm" padding="8px"
                    on:click={resetPassword} />
                {/if}  
            </div>
        </div>
</AdminComponent>