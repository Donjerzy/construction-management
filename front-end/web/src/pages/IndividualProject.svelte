<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    export let projectId;

    let active = 'overview';
    let appName = 'Mjengo Bora Construction'; //  overview | 
    let projectOverview = {
        projectName : "-",
        numberOfClients: 0,
        numberOfEmployees: 0,
        numberOfTasksDone: 0,
        numberOfTasksOngoing: 0,
        budgetAvailable: 0.0,
        budgetSpent: 0.0
    }


    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/project/overview?project=${projectId}`, {
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
                projectOverview = result.overview;
            }
        })
    });


    function navigate(to) {
        active = to;
    }


</script>


<svelte:head>
    <title>Project | {projectOverview.projectName}</title>
</svelte:head>

<AdminComponent appName={appName} userFirstName={get(firstName)} contentTitle={projectOverview.projectName}>
    {#if active === 'overview'}
    <div class="container">
        <div class="project-nav">
            <p id="active-link">Overview</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("clients")}>Clients</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("employees")}>Employees</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("tasks")}>Tasks</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("expenses")}>Expenses</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p on:click={()=> navigate("actions")}>Actions</p>
        </div>

        <div class="overview-table">
            <Table shadow>
                <TableHead defaultRow={false} theadClass="border-black">
                    <tr class="border-black bg-primary-50">
                        <TableHeadCell class="border-black border-collapse border text-white">Clients</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white">Employees</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white text-center " colspan="2">Tasks</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white text-center" colspan="2">Budget</TableHeadCell>
                    </tr>
                    <tr class="bg-primary-100">
                        <TableHeadCell class="border-black border-collapse border">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Done</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Open</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Available</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Spent</TableHeadCell>
                    </tr>
                </TableHead>
                <TableBody tableBodyClass="divide-y">
                    <TableBodyRow>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.numberOfClients}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.numberOfEmployees}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.numberOfTasksDone}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.numberOfTasksOngoing}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.budgetAvailable}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{projectOverview.budgetSpent}</TableBodyCell>
                    </TableBodyRow>
                </TableBody>
            </Table>
        </div> 
        
    </div>
    {/if}
</AdminComponent>


<style>
    .container {
        margin-top: 40px;
    }
    .project-nav {
        display: flex;
        justify-content: space-between;
        height: 20px;
        padding-right: 20px;
        align-items: center;
    }
    .project-nav p {
        text-decoration: underline;
        font-size: 1rem;
    }
    .project-nav p:hover {
        cursor: pointer;
        color: var(--tertiary-clr);
    }
    #active-link {
        color: var(--tertiary-clr);
        font-size: 1.2rem;
    }
    .overview-table {
        margin-top: 40px;
    }
    /* Table{
       border-collapse: collapse;
       width: 100%;
    }
    th, td {
        border: 1px black solid;
        width: 120px;
        height: 80px; 
        text-align: center;
    }
    .table-head,
    .table-attributes {
        height: 80px;
    } */
</style>