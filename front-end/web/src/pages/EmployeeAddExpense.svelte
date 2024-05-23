<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    let contentTitle = "Add Expense";

    import {firstName, accessToken, projectUUID} from '../stores.js' 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    const projectId = $page.params.projectId;
    let appName = "Mjengo Bora Construction";
    let title = "Add Expense";
    let loading = false;
    let expenseTitle;
    let chosenExpenseType;
    let expenseCost;
    let expenseCostDisplay;
    let date;
    let expenseDocumentChosen = false;
    let currPage = 'one';
    let expenseDocument = null;

    let expenseTypes = [
        {id: 1, name: "Materials and Supplies"},
        {id: 2, name: "Equipment and Machinery"},
        {id: 3, name: "Permits and Fees"},
        {id: 4, name: "Overhead and Administrative Costs"},
        {id: 5, name: "Site Preparation and Development"},
        {id: 6, name: "Other"},
    ]
    let note;

    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function updateExpenseCost(event) {
        expenseCost = event.target.value.replace(/,/g, '');
        expenseCostDisplay = formatWithCommas(expenseCost);
    }

    function removeExpenseDocument() {
        expenseDocument = null;
        expenseDocumentChosen = false;
    }

    function handleExpenseDocumentUpload(event) {
        expenseDocument = event.target.files[0];
        expenseDocumentChosen = true;
    }

    async function AddExpense(expense) {
        loading = true;
        let error = false;
        let existsError = false;
        let errorMessage = "";
        let data = new FormData();
        data.append('project', expense.projectId);
        data.append('cost', expense.cost);
        data.append('note', expense.note);    
        data.append('date', expense.date);  
        data.append('type', expense.type);  
        data.append('document', expense.document);
        data.append('title', expense.title);


        await fetch('http://localhost:8080/api/v1/employee/expense/add', {
            method: 'POST',
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
            },
            body: data
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
                notifications.success("Expense added successfully", 1000);
                expenseTitle = "";
                chosenExpenseType = "0";
                expenseCost = 0.0;
                expenseCostDisplay = "";
                date = "";
                expenseDocumentChosen = false;
                currPage = 'one';
                expenseDocument = null;
                currPage = "one"  // one || two
                return;
            }
        }).then((res)=> {
            if(existsError) {
                errorMessage = res.message
            }
        }).catch(error=> {
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

    function validateInput(event) {
        event.preventDefault();
        if(chosenExpenseType === undefined || chosenExpenseType === '0') {
            return notifications.danger("Expense type is a required field", 1000);
        }
        if(expenseTitle === undefined || expenseTitle.length === 0) {
            return notifications.danger("Title is a required field", 1000);
        }
        if(isNaN(parseFloat(expenseCost))|| expenseCost === undefined || parseFloat(expenseCost) <= 0 ) {
            return notifications.danger("Cost should be a number greater than zero", 1000);
        }
        if(date === undefined) {
            notifications.danger('Date is a required field', 1000);
            return;
        }
        if(note === undefined) {
            note = "";
        }
        AddExpense({
            projectId: get(projectUUID),
            cost: parseFloat(expenseCost),
            date: date,
            type: chosenExpenseType,
            title: expenseTitle,
            note: note,
            document: expenseDocument
        })
    }



</script>




<EmployeeComponent contentTitle={contentTitle}>
    <Toast />
    <form class="mt-4">
        {#if currPage === "one"}
            <div class="mt-2 flex flex-col gap-2">
                <label  for="expense_types" class="block font-serif">Expense Type</label>
                <select id="expense_types" class="hover:cursor-pointer font-sans" bind:value={chosenExpenseType}>
                    <option value="0">--Select--</option>
                    {#each expenseTypes as type }
                        <option value={type.id}>{type.name}</option>
                    {/each}
                </select>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label class="font-serif" for="expense_title">Title</label>
                <input name="expense_title" class="font-sans rounded border-primary-800" type="text" id="expense_title" bind:value={expenseTitle}>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label class="font-serif" for="cost">Cost</label>
                <input name="cost" class="font-sans rounded border-primary-800" type="text" id="cost" bind:value={expenseCostDisplay} on:input={updateExpenseCost}>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label class="font-serif" for="note">Note</label>
                <textarea name="note" class="font-sans rounded border-primary-800" bind:value={note} id="note"></textarea>
            </div>
            <div class="flex flex-col gap-2 mt-3">
                <Button height=12 width=36 label="Next" fontSize="sm" padding="8px" on:click={() => { currPage = "two"}}   />
            </div>
        {:else}
            <div class="flex flex-col gap-2 mt-2">
                <label class="font-serif" for="join_date">Date</label>
                <input name="join_date" class="font-sans rounded border-primary-800" type="date" id="join_date" bind:value={date}>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                {#if expenseDocumentChosen === false}
                    <label class="font-serif" for="contract">Expense Document (Not required*)</label>
                    <input accept=".pdf" name="contract" class="font-sans rounded border-primary-800" type="file" id="contract" on:change={handleExpenseDocumentUpload}>
                {:else}
                    <div class="flex gap-4 items-center">
                        <p class="font-bold text-lg font-serif">File:</p>
                        <p class="italic text-base font-sans">{expenseDocument.name}</p>
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <svg on:click={()=> removeExpenseDocument()} class="h-7 w-7 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21.12 22.54L19 20.41L16.88 22.54L15.47 21.12L17.59 19L15.47 16.88L16.88 15.47L19 17.59L21.12 15.47L22.54 16.88L20.41 19L22.54 21.12L21.12 22.54M14 2H6C4.89 2 4 2.89 4 4V20C4 21.11 4.89 22 6 22H13.81C13.28 21.09 13 20.05 13 19C13 15.69 15.69 13 19 13C19.34 13 19.67 13.03 20 13.08V8L14 2M13 9V3.5L18.5 9H13Z" /></svg>
                    </div>
                {/if}      
            </div> 
            <div class="flex flex-col gap-2 mt-3">
                <Button height=12 width=36 label="Previous" fontSize="sm" padding="8px" on:click={() => { currPage = "one"}}   />
            </div>
            
            <div class="mt-4 flex justify-center">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                        height=12 width=36 label="Add Expense" fontSize="sm" padding="8px"
                        on:click={validateInput} 
                    />
                {/if}  
            </div>
        {/if}
        
        
    </form>
</EmployeeComponent>