<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn, projectUUID} from '../stores.js' 
    import { get } from "svelte/store";
    import { Button } from 'flowbite-svelte';
    import { onMount } from "svelte";
    import { Modal } from 'flowbite-svelte';
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import Loader from "../components/loading-component.svelte";
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Profile";
    let projects = [];
    let defaultModal = false;
    let chosenProject;
    let loading = false;

    onMount(()=> {
            let errorFetch = false;
            fetch(`http://localhost:8080/api/v1/project/auth/list`, {
                headers: {
                    'Authorization': `Bearer ${get(accessToken)}`
                }
            })
            .then(response => {
                if(!response.ok) {
                    errorFetch = true;
                   firstName.set("");
                   accessToken.set("");
                   loggedIn.set("false");
                   window.location.replace('/'); 
                } else {
                    return response.json();
                }
            }).then((result)=> {
                if(!errorFetch) {
                    projects = result.projects;
                }
            })
    });


    function logOutUser() {
        fetch('http://localhost:8080/api/v1/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${get(accessToken)}`
            }
        });

        firstName.set("");
        accessToken.set("");
        loggedIn.set("false");
        window.location.replace('/'); 
    }


    function updateEmployeeLogIn(token, name, projectId) {
        loggedIn.set(true);
        accessToken.set(token);
        firstName.set(name);
        projectUUID.set(projectId)
    }


    async function switchToEmployeeModule() {
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
                    token: get(accessToken),
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
    <title>Profile</title>
</svelte:head>



<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <Toast />
    <Modal title="Projects" bind:open={defaultModal}>
        {#if projects.length <= 0}
            <div class="flex justify-center items-center mt-5 mb-5">
                <p class="text-base font-sans">You are not an employee in any project</p>
            </div>
        {:else}
            <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400 font-serif">
                Select project you wish to switch to.
            </p>
            <label for="et" class="mr-5 font-serif">Project</label>
            <select id="et" class="min-w-60 rounded" bind:value={chosenProject}>
                <option value="0">-- Select --</option>
                {#each projects as project }
                    <option value={project.id}>{project.name}</option>
                {/each}
            </select>
        {/if}
        
        <svelte:fragment slot="footer">
            {#if projects.length > 0}
                <div class="flex justify-center w-full">
                    {#if loading}
                        <Loader />
                    {:else}
                        <button on:click={switchToEmployeeModule}  class="w-28 h-10 bg-primary-50 p-2 rounded text-white hover:bg-primary-200 font-serif">Switch</button>  
                    {/if}
                </div>
            {/if}
            
        </svelte:fragment>
      </Modal>


    <div class="mt-2">
        <div class="mt-5">
            <p class="text-sm font-serif">Current Module</p>
            <div class="mt-2 border-black bg-primary-50 h-16 rounded p-4 flex justify-between items-center">
                <p class="font-sans text-base text-white">Admin module</p>
                <div class="flex items-center gap-4">
                    <p class="font-serif text-white">Switch to Employee Module</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                  <svg on:click={()=> defaultModal = true} class="fill-white h-6 w-6 hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21,9L17,5V8H10V10H17V13M7,11L3,15L7,19V16H14V14H7V11Z" /></svg>
                </div>
            </div>
        </div>


        <div class="mt-8">
            <Button on:click={logOutUser} class="w-fit" color="dark">Logout</Button> 
        </div>
    </div>
</AdminComponent>


