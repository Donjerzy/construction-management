<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    import {firstName, accessToken, loggedIn, projectUUID} from '../stores.js' 
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { get } from "svelte/store";
    let contentTitle = "Profile";
    let loading = false;


    async function logout() {
        loading = true;
        fetch('http://localhost:8080/api/v1/employee/auth/logout', {
            method: 'POST',
            headers: {
                "Cmt": `CMT ${get(accessToken)}`
            }
        });
        loading = false;
        firstName.set("");
        accessToken.set("");
        projectUUID.set("");
        loggedIn.set("false");
        window.location.replace('/');  
    }

    function updateLogIn(token, name) {
        loggedIn.set(true);
        accessToken.set(token);
        firstName.set(name);
        projectUUID.set("");
    }

    async function switchToAdminModule() {
        let error = false;
        await fetch('http://localhost:8080/api/v1/employee/auth/admin', {
            method: 'POST',
            headers: {
                "Cmt": `CMT ${get(accessToken)}`
            }
        }).then(response=> {
            if(!response.ok) {
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



</script>


<EmployeeComponent contentTitle={contentTitle}>
    <!-- <div class="mt-2">
        <div class="mt-5">
            <p class="text-sm font-serif">Current Module</p>
            <div class="mt-2 border-black  h-16 rounded p-4 flex justify-between items-center bg-primary-100">
                <p class="font-sans text-base">Employee module</p>
                <div class="flex items-center gap-4">
                    <p class="font-serif">Switch to Admin Module</p>
                    
                  <svg on:click={switchToAdminModule}  class="h-6 w-6 hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21,9L17,5V8H10V10H17V13M7,11L3,15L7,19V16H14V14H7V11Z" /></svg>
            </div>
        </div>
    </div> -->

    
    <div class="mt-5">
        {#if loading}
            <Loader />
        {:else}
            <Button 
                height=10 width=32 label="Logout" fontSize="sm" padding="8px"
                on:click={logout} />
        {/if}
    </div>
</EmployeeComponent>