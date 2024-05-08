<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js' 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    let originalClient = get(projectClient);
    // let newClient = get(projectClient);
    const projectId = $page.params.projectId;
    let appName = "Mjengo Bora Construction";
    let title = "Edit Client";
    let loading = false;
    let name = originalClient.name;
    let typeOfClient = originalClient.type === 'INDIVIDUAL' ? 'individual' : originalClient.type === "ORGANISATION" ? 'organisation' : 'group'                    
    let investedAmount = originalClient.investedAmount;
    let committedAmount = originalClient.committedAmount;
    let committedAmountDisplay = formatWithCommas(originalClient.committedAmount);
    let investedAmountDisplay = formatWithCommas(originalClient.investedAmount);


    
    function hasOnlyLetters(inputString) {
        return /^[a-zA-Z\s]*$/.test(inputString);
    }


    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function updateCommitedAmount(event) {
        committedAmount = event.target.value.replace(/,/g, '');
        committedAmountDisplay = formatWithCommas(committedAmount);
    }

    function updateInvestedAmount(event) {
        investedAmount = event.target.value.replace(/,/g, '');
        investedAmountDisplay = formatWithCommas(investedAmount);
    }




    async function EditClient(client) {
        loading = true;
        let error = false;
        let existsError = false;
        let errorMessage = "";
        await fetch('http://localhost:8080/api/v1/project/edit-client', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify(client)
        }).then(response=> {
            loading = false;
            if(response.status === 400) {
                existsError = true;
                return response.json()
            }
            if(!response.ok) {
                error = true;
                return
            } else {
                error = false;
                existsError = false;
                notifications.success("Client edited successfully", 1000);
                window.location.replace(`/project/${projectId}`);
                return;
            }
        }).then((res)=> {
            if(existsError) {
                errorMessage = res.message
            }
        }) 
        .catch(error=> {
            loading = false;
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


    function validateInput(e) {
        e.preventDefault();
        if(name.length === 0) {
            notifications.danger("Client name cannot be empty", 1000);
            return;
        }
        if(!hasOnlyLetters(name)) {
            notifications.danger("Client name has invalid characters", 1000);
            return;
        }
        if(typeOfClient === undefined || typeOfClient.length === 0) {
            notifications.danger("Client type is a required field", 1000);
            return;
        }
        if(isNaN(parseFloat(committedAmount))) {
            notifications.danger("Committed amount must be a number", 1000);
            return;
        }
        if(committedAmount < 0) {
            notifications.danger("Committed must be greater than zero", 1000);
            return;
        }
        if(isNaN(parseFloat(investedAmount))) {
            notifications.danger("Invested amount must be a number", 1000);
            return;
        }
        if(investedAmount < 0) {
            notifications.danger("Committed must be greater than zero", 1000);
            return;
        }
        if(investedAmount > committedAmount) {
            notifications.danger("Invested amount must be less than what was committed", 1000);
            return;
        }
        if (originalClient.name === name 
            && originalClient.committedAmount === committedAmount 
            && originalClient.investedAmount === investedAmount
            && originalClient.type === typeOfClient) {
            notifications.danger("Update the client before saving", 1000);
            return;    
        }
        EditClient({
            clientId: originalClient.id,
            name: name,
            type: typeOfClient,
            project: projectId,
            investedAmount: parseFloat(investedAmount),
            committedAmount: parseFloat(committedAmount)
        });
    }




</script>


<Toast />


<AdminComponent appName={appName} contentTitle={title} userFirstName={get(firstName)}>
    <div class="container">
        <form class="mt-7">
            <div class="flex flex-col gap-2 mt-1">
                <label for="client_name">Client Name</label>
                <input name="name" class="rounded border-primary-800" type="text" id="client_name" bind:value={name}>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label for="client_type">Client Type</label>
                <select name="client_type" class="rounded border-primary-800" bind:value={typeOfClient} id="client_type">
                    <option class="h-4" value="individual">Individual</option>
                    <option class="h-4" value="group">Group</option>
                    <option class="h-4" value="organisation">Organisation</option>
                </select>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label for="commited_amount">Committed Amount</label>
                <input name="commited_amount" class="rounded border-primary-800" type="text" id="commited_amount" bind:value={committedAmountDisplay} on:input={updateCommitedAmount}>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label for="invested_amount">Invested Amount</label>
                <input name="invested_amount" class="rounded border-primary-800" type="text" id="invested_amount" bind:value={investedAmountDisplay} on:input={updateInvestedAmount}>
            </div>
            <div class="mt-4">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                    height=12 width=36 label="Save" fontSize="sm" padding="8px"
                    on:click={validateInput} />
                {/if}    
            </div>
        </form>
    </div>
</AdminComponent>