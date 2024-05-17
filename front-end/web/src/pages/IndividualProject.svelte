<script>
    import AdminComponent from "../components/admin-component.svelte";
    import Button from '../components/button.svelte';
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import { notifications } from "../lib/notification";
    import Loader from "../components/loading-component.svelte";
    export let projectId;


    
    let reports = [{ id: 1 ,name: "General Project Report"}]


    let reportSearchTerm = '';
    
    $: filteredReports = reports.filter((report) => report.name.toLowerCase().indexOf(reportSearchTerm.toLowerCase()) !== -1);


    let clients = [];
    let projectCode = "56TRT-SUGUFSO-SFGSVF";
    let employees = [];
    let active = 'overview';
    let appName = 'Mjengo Bora Construction'; //  overview | 
    let projectOverview = {
        projectName : "-",
        numberOfClients: 0,
        numberOfEmployees: 0,
        numberOfTasksDone: 0,
        numberOfTasksOngoing: 0,
        projectBudgetReceived:0.0,
        budgetAvailable: 0.0,
        budgetSpent: 0.0
    }
    let toDoTasks = [];
    let inProgressTasks = [];
    let doneTasks = [];
    let searchTerm = '';
    let bigLoading = false;
    
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


        fetch(`http://localhost:8080/api/v1/project/code?project=${projectId}`, {
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
                projectCode = result.code;
            }
        });

        fetch(`http://localhost:8080/api/v1/task/project/list?project=${projectId}`, {
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
                toDoTasks = result.tasks.filter((task)=> task.status === 'todo');
                inProgressTasks = result.tasks.filter((task)=> task.status === 'in_progress');
                doneTasks = result.tasks.filter((task)=> task.status === 'done');
            }
        });

    
    });


    async function fetchTasks() {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/task/project/list?project=${projectId}`, {
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
                toDoTasks = result.tasks.filter((task)=> task.status === 'todo');
                inProgressTasks = result.tasks.filter((task)=> task.status === 'in_progress');
                doneTasks = result.tasks.filter((task)=> task.status === 'done');
                bigLoading = false;
            }
        });
    }


    function navigate(to) {
        active = to;
    }

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function copyProjectCode() {
        console.log('clicked');
        navigator.clipboard.writeText(projectCode);
        notifications.success('Project code copied', 1000);
    }

    async function moveTask(taskId,to) {
        bigLoading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/task/move', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify({
                taskId: taskId,
                action: to 
            })
        }).then(response=> {
            if(response.status === 400) {
                bigLoading = false;
                existsError = true;
                return
            }
            if(!response.ok) {
                bigLoading = false;
                error = true;
                return
            } else {
                error = false;
                existsError = false;
                fetchTasks();
                return;
            }
        }).catch(error=> {
            bigLoading = false;
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


</script>


<svelte:head>
    <title>Project | {projectOverview.projectName}</title>
</svelte:head>

<AdminComponent appName={appName} userFirstName={get(firstName)} contentTitle={projectOverview.projectName}>
    <Toast />
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
            <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
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
                        <TableHeadCell class="border-black border-collapse border text-white text-center" colspan="3">Budget</TableHeadCell>
                    </tr>
                    <tr class="bg-primary-100">
                        <TableHeadCell class="border-black border-collapse border">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Done</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Open</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border">Received</TableHeadCell>
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
                        <TableBodyCell class="border-black border-collapse border">{numberWithCommas(projectOverview.projectBudgetReceived)}</TableBodyCell>
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
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
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
                                    <TableBodyCell class="flex gap-8"><a on:click={()=> {
                                        projectClient.set({
                                            id: client.id,
                                            name: client.name,
                                            type: client.type,
                                            committedAmount: client.committedAmount,
                                            investedAmount: client.investedAmount
                                        })
                                    } } class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/edit-client`}>Edit</a>
                                    
                                    <a class="underline hover:cursor-pointer hover:text-primary-200" href={`/${client.id}/receive-investment`}>Receive investment</a>
                                </TableBodyCell>
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
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
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
                                                <a class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/${employee.id}`}>View</a>
                                            </div>
                                            
                                        </TableBodyCell>
                                    </TableBodyRow>
                                {/each}
                            </TableBody>
                    </Table> 
                    </TableSearch>
                </div>
            </div>
        {:else if active === 'actions'}
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
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("actions")}>Actions</p>
                </div>

                <div class="mt-5">
                    <p class="text-sm">Project Code</p>
                    <div class="mt-2 border-black bg-white h-16 rounded p-4 flex justify-between items-center">
                        <p>{projectCode}</p>
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <svg on:click={copyProjectCode} class="w-5 h-5 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z" /></svg>
                    </div>
                </div>
            </div>    
        {:else if active === 'tasks'}    
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
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-2 flex justify-end">
                    <a href={`/project/${projectId}/add-task`}><Button fontSize="base" height="10" label="Add Task" padding="7" width="32" /> </a>
                </div>
                <div id="task-tracks">
                    {#if bigLoading}
                        <div class="mt-20">
                            <Loader />
                        </div>
                    {:else}
                    <div id="to-do">
                        <div>
                            <p class="-rotate-90 mr-3">TODO</p>
                        </div>
                        {#each toDoTasks as task }
                            <div class="flex border border-black flex-col justify-between bg-primary-50 text-white h-full  pt-4 pl-2 pr-4 pb-2 min-w-[160px] max-w-[200px] rounded-md   mr-20 text-ellipsis text-nowrap">
                                <div class="overflow-hidden text-base">{task.title}</div>
                                <div class="flex gap-3">
                                    <p class="text-white underline hover:cursor-pointer hover:text-primary-200">View</p>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                    <svg on:click={()=> moveTask(task.taskId, "in_progress")} class="h-6 w-6 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M11,4H13V16L18.5,10.5L19.92,11.92L12,19.84L4.08,11.92L5.5,10.5L11,16V4Z" /></svg>
                                </div>  
                            </div>
                        {/each}  
                    </div>
                    <div id="in-progress">
                        <div>
                            <p class="-rotate-90 mr-3">IN-PROGRESS</p>
                        </div>
                        {#each inProgressTasks as task }
                            <div class="flex border border-black flex-col justify-between bg-primary-50 text-white h-full  pt-4 pl-2 pr-4 pb-2 min-w-[160px] max-w-[200px] rounded-md   mr-20 text-ellipsis text-nowrap ">
                                <div class="overflow-hidden text-base">{task.title}</div>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <div class="flex gap-3">
                                        <p class="text-white underline hover:cursor-pointer hover:text-primary-200">View</p>
                                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                                        <svg on:click={()=> moveTask(task.taskId, "done")} class="h-6 w-6 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M11,4H13V16L18.5,10.5L19.92,11.92L12,19.84L4.08,11.92L5.5,10.5L11,16V4Z" /></svg>
                                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                                        <svg on:click={()=> moveTask(task.taskId, "todo")} class="h-6 w-6 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M13,20H11V8L5.5,13.5L4.08,12.08L12,4.16L19.92,12.08L18.5,13.5L13,8V20Z" /></svg>
                                    </div>            
                            </div> 
                        {/each} 
                    </div>
                    <div id="done">
                        <div>
                            <p class="-rotate-90 mr-3">DONE</p>
                        </div>
                        {#each doneTasks as task}
                            <div class="flex border border-black flex-col justify-between bg-primary-50 text-white h-full  pt-4 pl-2 pr-4 pb-2 min-w-[160px] max-w-[200px] rounded-md   mr-20 text-ellipsis text-nowrap ">
                                <div class="overflow-hidden text-base">{task.title}</div>
                                <!-- svelte-ignore a11y-no-static-element-interactions -->
                                <div class="flex gap-3">
                                    <p class="text-white underline hover:cursor-pointer hover:text-primary-200">View</p>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <svg on:click={()=> moveTask(task.taskId, "in_progress")} class="h-6 w-6 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M13,20H11V8L5.5,13.5L4.08,12.08L12,4.16L19.92,12.08L18.5,13.5L13,8V20Z" /></svg>
                                </div> 
                            </div> 
                        {/each}  
                    </div>
                {/if}    
                </div>   
            </div>
        {:else if active === 'reports'}     
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
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-5">
                    <TableSearch placeholder="Search by report name" hoverable={true} bind:inputValue={reportSearchTerm}>
                        <Table divClass="max-h-80 overflow-auto" shadow>
                             <TableHead defaultRow={false} theadClass="border-black">
                                 <tr class="bg-primary-100">
                                     <TableHeadCell class="text-white">#</TableHeadCell>
                                     <TableHeadCell class="text-white">Name</TableHeadCell>
                                     <TableHeadCell class="text-white">Action</TableHeadCell>
                                 </tr>
                             </TableHead>
                             <TableBody>
                                 {#each filteredReports as report}
                                     <TableBodyRow>
                                         <TableBodyCell>{report.id}</TableBodyCell>
                                         <TableBodyCell>{report.name}</TableBodyCell>
                                         <TableBodyCell class="flex gap-8">                
                                             <a target="_blank" class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/report/general`}>View</a>
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
        height: 70%;
        max-height: 70%;
    }
    #active-link {
        color: var(--tertiary-clr);
        font-size: 1.2rem;
    }
    .overview-table {
        margin-top: 40px;
    }
    #task-tracks {
        display: grid;
        grid-template-rows: 32% 32% 32%;
        grid-template-areas: 
            "todo"
            "in-progress"
            "done";
        margin-top: 20px;
        height: 100%;
        max-height: 100%;
    }
    #to-do {
        grid-area: todo;
        border: 1px solid #ccc;
        padding-left: 8px; 
        display: flex; 
        flex-wrap: nowrap; 
        overflow-x: auto;
        align-items: center;
        padding-top: 12px;
        padding-bottom: 12px;
        border-radius: 4px;
        padding-right: 8px;
        background-color: aliceblue;
    }
    #in-progress {
        grid-area: in-progress;
        border: 1px solid #ccc;
        display: flex; 
        flex-wrap: nowrap; 
        overflow-x: auto;
        align-items: center;
        padding-top: 12px;
        padding-bottom: 12px;
        border-radius: 4px;
        padding-right: 8px;
        background-color: aliceblue;
    }
    #done {
        grid-area: done;
        border: 1px solid #ccc;
        padding-left: 8px; 
        display: flex; 
        flex-wrap: nowrap; 
        overflow-x: auto;
        align-items: center;
        padding-top: 12px;
        padding-bottom: 12px;
        border-radius: 4px;
        padding-right: 8px;
        background-color: aliceblue;
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