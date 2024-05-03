<script>
    import AdminComponent from "../components/admin-component.svelte";
    import Button from '../components/button.svelte';
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    export let projectId;

    let clients = [];
    let employees = [];
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
    let searchTerm = '';
    
    $: filteredItems = clients.filter((client) => client.name.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1);

    $: filteredEmployees = employees.filter((employee) => employee.name.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1);


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


        fetch(`http://localhost:8080/api/v1/project/get-clients?project=${projectId}`, {
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
                clients = result.clients;
            }
        })


        fetch(`http://localhost:8080/api/v1/project/get-employees?project=${projectId}`, {
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
                employees = result.employees;
            }
        })

    
    });


    function navigate(to) {
        active = to;
    }

    function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


</script>


<svelte:head>
    <title>Project | {projectOverview.projectName}</title>
</svelte:head>

<AdminComponent appName={appName} userFirstName={get(firstName)} contentTitle={projectOverview.projectName}>
    {#if active === 'overview'}
    <div class="container">
        <div class="flex justify-between h-8 align-middle text-base">
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("overview")}>Overview</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("employees")}>Employees</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
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
                        <TableBodyCell class="border-black border-collapse border">{numberWithCommas(projectOverview.budgetAvailable)}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border">{numberWithCommas(projectOverview.budgetSpent)}</TableBodyCell>
                    </TableBodyRow>
                </TableBody>
            </Table>
        </div> 
        
    </div>
    {:else if active === "clients"}
        <div class="container">
            <div class="flex justify-between h-8 align-middle text-base">
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("clients")}>Clients</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("employees")}>Employees</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
            </div>
            <div class="flex mt-5 justify-end">
               <a href={`/project/${projectId}/add-client`}><Button fontSize="base" height="10" label="Add Client" padding="7" width="32" /> </a>
            </div>
            <div class="mt-4 pb-8 max-h-screen">
                <TableSearch placeholder="Search by client name" hoverable={true} bind:inputValue={searchTerm}>
                   <Table divClass="max-h-80 overflow-auto" shadow>
                        <TableHead defaultRow={false} theadClass="border-black">
                            <tr class="bg-primary-100">
                                <TableHeadCell class="text-white">Name</TableHeadCell>
                                <TableHeadCell class="text-white">Type</TableHeadCell>
                                <TableHeadCell class="text-white">Committed Amount</TableHeadCell>
                                <TableHeadCell class="text-white">Invested Amount</TableHeadCell>
                                <TableHeadCell class="text-white">Action</TableHeadCell>
                            </tr>
                        </TableHead>
                        <TableBody>
                            {#each filteredItems as client}
                                <TableBodyRow>
                                    <TableBodyCell>{client.name}</TableBodyCell>
                                    <TableBodyCell>{client.type}</TableBodyCell>
                                    <TableBodyCell>{numberWithCommas(client.committedAmount)}</TableBodyCell>
                                    <TableBodyCell>{numberWithCommas(client.investedAmount)}</TableBodyCell>
                                    <TableBodyCell><a on:click={()=> {
                                        projectClient.set({
                                            id: client.id,
                                            name: client.name,
                                            type: client.type,
                                            committedAmount: client.committedAmount,
                                            investedAmount: client.investedAmount
                                        })
                                    } } class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/edit-client`}>Edit</a></TableBodyCell>
                                </TableBodyRow>
                            {/each}
                        </TableBody>
                   </Table> 
                </TableSearch>
            </div>
        </div>
        {:else if active === "employees"}
            <div class="container">
                <div class="flex justify-between h-8 align-middle text-base">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="flex mt-5 justify-end">
                <a href={`/project/${projectId}/add-employee`}><Button fontSize="base" height="10" label="Add Employee" padding="7" width="32" /> </a>
                </div>
                <div class="mt-4 pb-8 max-h-screen">
                    <TableSearch placeholder="Search by employee name" hoverable={true} bind:inputValue={searchTerm}>
                    <Table divClass="max-h-80 overflow-auto" shadow>
                            <TableHead defaultRow={false} theadClass="border-black">
                                <tr class="bg-primary-100">
                                    <TableHeadCell class="text-white">Name</TableHeadCell>
                                    <TableHeadCell class="text-white">Employee Type</TableHeadCell>
                                    <TableHeadCell class="text-white">Wage Type</TableHeadCell>
                                    <TableHeadCell class="text-white">Wage</TableHeadCell>
                                    <TableHeadCell class="text-white">Action</TableHeadCell>
                                </tr>
                            </TableHead>
                            <TableBody>
                                {#each filteredEmployees as employee}
                                    <TableBodyRow>
                                        <TableBodyCell>{employee.name}</TableBodyCell>
                                        <TableBodyCell>{employee.employeeType}</TableBodyCell>
                                        <TableBodyCell>{employee.wageType}</TableBodyCell>
                                        <TableBodyCell>{numberWithCommas(employee.wage)}</TableBodyCell>
                                        <TableBodyCell>
                                            <div class="flex gap-4 items-center">
                                                <a class="underline hover:cursor-pointer hover:text-primary-200" href={`/`}>View</a>
                                                <a class="underline hover:cursor-pointer hover:text-primary-200" href={`/`}>Edit</a>
                                            </div>
                                            
                                        </TableBodyCell>
                                    </TableBodyRow>
                                {/each}
                            </TableBody>
                    </Table> 
                    </TableSearch>
                </div>
            </div>
        {/if}
</AdminComponent>


<style>
    .container {
        margin-top: 40px;
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