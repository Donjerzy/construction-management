<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    const clientId = $page.params.clientId;
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Receive Investment"
    let loading = false;
    let remainingAmount = 0.0;
    let client = "";
    let totalCommitted = 0.0;
    let totalInvested = 0.0;
    let amountReceived = 0.0;
    let amountReceivedDisplay = "";


    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function updateAmountReceived(event) {
        amountReceived = event.target.value.replace(/,/g, '');
        amountReceivedDisplay = formatWithCommas(amountReceived);
    }

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/client/expected-investment?client=${clientId}`, {
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
                client = result.expectedInvestment.client;
                remainingAmount = result.expectedInvestment.remainingAmount;
                totalCommitted = result.expectedInvestment.totalCommitted;
                totalInvested = result.expectedInvestment.totalInvested;
            }
        })
    });

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }


    async function validateInput() {
        if(isNaN(parseFloat(amountReceived)))  {
           return notifications.danger("Invalid amount",1000);
        }
        if(parseFloat(amountReceived) > parseFloat(remainingAmount)) {
            return notifications.danger("Received amount cannot be greater than remaining amount",1000);
        }
        if(parseFloat(amountReceived) <= 0) {
            return notifications.danger("Received amount cannot be less than or equal to zero",1000);
        }

        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/client/receive-investment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify(
                {
                    client: clientId,
                    amount: parseFloat(amountReceived)
                }
            )
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
                notifications.success("Investement recorded successfully", 1000);
                amountReceived = 0.0;
                amountReceivedDisplay = "";
                window.location.reload();
                return;
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger("An error occurred try again", 1000); 
        }
        if(error) {
            firstName.set("");
            accessToken.set("");
            loggedIn.set("false");
            window.location.replace('/'); 
        }
    }
</script>


<AdminComponent appName={appName} userFirstName={get(firstName)} contentTitle={contentTitle}>
    <Toast />
    <div class="mt-10">
        <div class="flex gap-20 items-center w-full">
            <div class="flex-col gap-40 w-full">
                <p class="font-serif text-sm">Client</p>
                <p class="w-full h-10 font-sans italic border mt-1 border-primary-100 p-2 bg-primary-100 rounded">{client}</p>
            </div>
            <div class="flex-col gap-40 w-full">
                <p class="font-serif text-sm">Committed Amount</p>
                <p class="w-full h-10 font-sans italic border mt-1 border-primary-100 p-2 bg-primary-100 rounded">{isNaN(totalCommitted) ? 0 : numberWithCommas(totalCommitted)}</p>
            </div>
        </div>

        <div class="flex gap-20 items-center w-full mt-5">
            <div class="flex-col gap-40 w-full">
                <p class="font-serif text-sm">Invested Amount</p>
                <p class="font-sans w-full h-10 italic border mt-1 border-primary-100 p-2 bg-primary-100 rounded">{isNaN(totalInvested) ? 0 : numberWithCommas(totalInvested)}</p>
            </div>
            <div class="flex-col gap-40 w-full">
                <p class="font-serif text-sm">Remaining Amount</p>
                <p class="font-sans italic border mt-1 h-10 w-full border-primary-100 p-2 bg-primary-100 rounded">{isNaN(remainingAmount) ? 0 : numberWithCommas(remainingAmount)}</p>
            </div>
        </div>
        <div class="flex flex-col gap-2 mt-5 w-full">
            <label class="font-serif text-sm" for="wage">Amount Received</label>
            <input name="wage" class="w-[560px] font-sans rounded border-primary-800" type="text" id="wage" bind:value={amountReceivedDisplay} on:input={updateAmountReceived}  >
        </div> 
        <div class="mt-5">
            {#if loading}
                <Loader />
            {:else}
                <Button
                height=10 width=36 label="Save" fontSize="sm" padding="8px"
                on:click={validateInput} />
            {/if} 
        </div>
    </div>
</AdminComponent>