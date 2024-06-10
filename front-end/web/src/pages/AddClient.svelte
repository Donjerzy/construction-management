<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import { Button } from 'flowbite-svelte';
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
   

    const projectId = $page.params.projectId;
    let appName = "Mjengo Bora Construction";
    let title = "Add Client";
    let loading = false;
    let name = "";
    let typeOfClient;
    let investedAmountDisplay = '0';
    let commitedAmountDisplay = '0';

    let investedAmount = '0';
    let commitedAmount = '0';

    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
   
    function updateCommittedAmount(event) {
        commitedAmount = event.target.value.replace(/,/g, '');
        commitedAmountDisplay = formatWithCommas(commitedAmount);
    }

    function updateInvestedAmount(event) {
        investedAmount = event.target.value.replace(/,/g, '');
        investedAmountDisplay = formatWithCommas(investedAmount);
    }

    function hasOnlyLetters(inputString) {
        return /^[a-zA-Z\s]*$/.test(inputString);
    }

    async function AddClient(client) {
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/client/add', {
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
                return
            }
            if(!response.ok) {
                error = true;
                return
            } else {
                error = false;
                existsError = false;
                notifications.success("Client added successfully", 1000);
                name = "";
                investedAmountDisplay = '0';
                commitedAmountDisplay = '0';
                return;
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger("Client Already Exists", 1000); 
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
        if(typeOfClient === undefined) {
            notifications.danger("Client type is a required field", 1000);
            return;
        }
        if(isNaN(parseFloat(commitedAmount))) {
            notifications.danger("Committed amount must be a number", 1000);
            return;
        }
        if(parseFloat(commitedAmount) < 0) {
            notifications.danger("Committed must be greater than zero", 1000);
            return;
        }
        if(isNaN(parseFloat(investedAmount))) {
            notifications.danger("Invested amount must be a number", 1000);
            return;
        }
        if(parseFloat(investedAmount) < 0) {
            notifications.danger("Committed must be greater than zero", 1000);
            return;
        }
        if(parseFloat(investedAmount) > parseFloat(commitedAmount)) {
            notifications.danger("Invested amount must be less than what was committed", 1000);
            return;
        }
        AddClient({
            name: name,
            type: typeOfClient,
            project: projectId,
            investedAmount: parseFloat(investedAmount),
            committedAmount: parseFloat(commitedAmount)
        });
    }




</script>


<Toast />


<AdminComponent appName={appName} contentTitle={title} userFirstName={get(firstName)}>
    <div class="container">
        <form class="mt-7">
            <div class="flex gap-20 items-center w-full">
                <div class="flex flex-col gap-2 mt-1 w-full">
                    <label for="client_name" class="font-serif text-sm">Client Name</label>
                    <input name="name" class="font-sans rounded border-primary-800 w-full h-10" type="text" id="client_name" bind:value={name}>
                </div>
                <div class="flex flex-col gap-2 mt-1 w-full">
                    <label class="font-serif text-sm" for="client_type">Client Type</label>
                    <select name="client_type" class="font-sans rounded border-primary-800 w-full h-10" bind:value={typeOfClient} id="client_type">
                        <option class="h-4" value="individual">Individual</option>
                        <option class="h-4" value="group">Group</option>
                        <option class="h-4" value="organisation">Organisation</option>
                    </select>
                </div>
            </div>

            <div class="flex gap-20 items-center mt-5 w-full">
                <div class="flex flex-col gap-2 w-full">
                    <label class="font-serif text-sm" for="commited_amount">Committed Amount</label>
                    <input name="commited_amount" class="w-full h-10 font-sans rounded border-primary-800" type="text" id="commited_amount" bind:value={commitedAmountDisplay} on:input={updateCommittedAmount} >
                </div>
                <div class="flex flex-col gap-2 w-full">
                    <label class="font-serif text-sm" for="invested_amount">Invested Amount</label>
                    <input name="invested_amount" class="w-full h-10 font-sans rounded border-primary-800" type="text" id="invested_amount" bind:value={investedAmountDisplay} on:input={updateInvestedAmount}>
                </div>
            </div>
            <div class="mt-5">
                {#if loading}
                    <Loader />
                {:else}
                <Button color="dark" class="w-fit" on:click={validateInput}>Add Client</Button>
                {/if} 
            </div>
        </form>
    </div>
</AdminComponent>