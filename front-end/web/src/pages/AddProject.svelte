<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte'
    let appName = "Mjengo Bora Construction";
    let title = "Add Project";
    let loading = false;
    let projectName = "";
    

    function hasOnlyLetters(inputString) {
        return /^[a-zA-Z\s]*$/.test(inputString);
    }

    async function addProject(name) {
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/project/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify( {
                name: name.toLowerCase()
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
                notifications.success("Project added successfully", 1000);
                projectName = "";
                return;
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger("Project Already Exists", 1000); 
        }
        if(error) {
            firstName.set("");
            accessToken.set("");
            loggedIn.set("false");
            window.location.replace('/'); 
        }
        
    }


    function validateInput() {
        if(projectName.length === 0) {
            notifications.danger("Project name cannot be empty", 1000);
            return;
        }
        if(!hasOnlyLetters(projectName)) {
            notifications.danger("Project name has invalid characters", 1000);
            return;
        }
        addProject(projectName);
    }




</script>


<Toast />


<AdminComponent appName={appName} contentTitle={title} userFirstName={get(firstName)}>
    <div class="container">
        <form>
            <div class="form-roww">
                <label class="font-serif text-sm" for="project_name">Project Name</label>
                <input class="rounded font-sans border-primary-800 w-1/2" type="text" id="project_name" bind:value={projectName}>
            </div>
            <div class="form-row">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                    height=10 width=36 label="Add Project" fontSize="sm" padding="8px"
                    on:click={validateInput} />
                {/if}
                
            </div>
        </form>
    </div>
</AdminComponent>


<style>
    .container {
        border: none;
        padding: 20px;
        margin-top: 20px;
        min-height: 50%;
    }
    .form-roww {
        margin-top: 8px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 12px; 
    }
    .form-row {
        margin-top: 20px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 12px;
        margin-bottom: 20px;
    }
</style>