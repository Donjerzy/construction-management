<script>
    import AdminComponent from "../components/admin-component.svelte";
    import { Button } from 'flowbite-svelte';
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch, Toggle } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import { notifications } from "../lib/notification";
    import Loader from "../components/loading-component.svelte";
    export let projectId;


    let taskSplit = 'unassigned'

    let view = false;


    
    let reports = [{ id: 1 ,name: "General Project Report"}, { id: 2 ,name: "Client Report"}]


    let reportSearchTerm = '';
    
    $: filteredReports = reports.filter((report) => report.name.toLowerCase().indexOf(reportSearchTerm.toLowerCase()) !== -1);



    let taskSearchTerm = '';
    
    $: filteredTasks = assignedTasks.filter((task) => task.title.toLowerCase().indexOf(taskSearchTerm.toLowerCase()) !== -1);

    function sortByPriority() {
        let low = []
        let medium = []
        let high = []

        for (const x of filteredTasks) {
            if(x.priority.toLowerCase() === 'low') {
                low.push(x)
            } else if (x.priority.toLowerCase() === 'medium') {
                medium.push(x)
            } else {
                high.push(x)
            }
        }
        filteredTasks = low.concat(medium).concat(high);
    }

    function sortByStatus() {
        let done = []
        let todo = []
        let in_progress = []

        for (const x of filteredTasks) {
            if(x.status.toLowerCase() === 'todo') {
                todo.push(x)
            } else if (x.status.toLowerCase() === 'in_progress') {
                in_progress.push(x)
            } else {
                done.push(x)
            }
        }
        filteredTasks = todo.concat(in_progress).concat(done);
    }


    let expenseSearchTerm = '';
    let expenses = [];


    let unassignedTaskSearchTerm = '';
    let unassignedTasks = [];
    $: filteredUnassignedTasks = unassignedTasks.filter((task) => task.title.toLowerCase().indexOf(unassignedTaskSearchTerm.toLowerCase()) !== -1);


    $: filteredExpenses = expenses.filter((expense) => expense.title.toLowerCase().indexOf(expenseSearchTerm.toLowerCase()) !== -1);


    let clients = [];
    let assignedTasks = [];
    let projectCode = "56TRT-SUGUFSO-SFGSVF";
    let employees = [];
    let chosenStatus = '';
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
    let projectStatus = '';
    
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

        fetch(`http://localhost:8080/api/v1/task/project/assigned?project=${projectId}`, {
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
                assignedTasks = result.tasks;
            }
        });

        fetch(`http://localhost:8080/api/v1/task/project/unassigned?project=${projectId}`, {
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
                unassignedTasks = result.tasks
            }
        });

        fetch(`http://localhost:8080/api/v1/expense/all?project=${projectId}`, {
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
                expenses = result.expenses;
            }
        });

        fetch(`http://localhost:8080/api/v1/project/status?projectId=${projectId}`, {
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
                projectStatus = result.status;
                chosenStatus = result.status.toLowerCase();
            }
        });

    });


    async function fetchTasks() {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/task/project/assigned?project=${projectId}`, {
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
                assignedTasks = result.tasks;
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

    async function changeStatus() {
        if (chosenStatus.toUpperCase != projectStatus) {
            let error = false;
            let existsError = false;
            await fetch('http://localhost:8080/api/v1/project/change-status', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${get(accessToken)}`
                },
                body: JSON.stringify({
                    projectId: projectId ,
                    status: chosenStatus
                })
            }).then(response=> {
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
                    refetchStatus();
                    return;
                }
            }).catch(error=> {
                notifications.danger("Could make request to server", 1000)
            })
            if(existsError) {
                notifications.danger("Error encountered", 1000); 
            }
            if(error) {
                firstName.set("");
                accessToken.set("");
                loggedIn.set("false");
                window.location.replace('/'); 
            }
        }
    }


    async function refetchStatus() {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/project/status?projectId=${projectId}`, {
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
                projectStatus = result.status;
                chosenStatus = result.status.toLowerCase();
                notifications.success("Status changed", 1000);
            }
        });

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
            notifications.danger("Error encountered", 1000); 
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
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("overview")}>Overview</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("employees")}>Employees</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
            <p class="underline text-primary-900 font-serif hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
        </div>

        <div class="overview-table">
            <Table shadow>
                <TableHead defaultRow={false} theadClass="border-black">
                    <tr class="border-black bg-primary-50">
                        <TableHeadCell class="border-black border-collapse border text-white font-serif">Clients</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white font-serif">Employees</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white text-center font-serif" colspan="2">Tasks</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border text-white text-center font-serif" colspan="3">Budget</TableHeadCell>
                    </tr>
                    <tr class="bg-primary-100">
                        <TableHeadCell class="border-black border-collapse border font-serif">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">No</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">Done</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">Open</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">Received</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">Available</TableHeadCell>
                        <TableHeadCell class="border-black border-collapse border font-serif">Spent</TableHeadCell>
                    </tr>
                </TableHead>
                <TableBody tableBodyClass="divide-y">
                    <TableBodyRow>
                        <TableBodyCell class="border-black border-collapse border font-sans">{projectOverview.numberOfClients}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{projectOverview.numberOfEmployees}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{projectOverview.numberOfTasksDone}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{projectOverview.numberOfTasksOngoing}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{numberWithCommas(projectOverview.projectBudgetReceived)}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{numberWithCommas(projectOverview.budgetAvailable)}</TableBodyCell>
                        <TableBodyCell class="border-black border-collapse border font-sans">{numberWithCommas(projectOverview.budgetSpent)}</TableBodyCell>
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
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("clients")}>Clients</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("employees")}>Employees</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
            </div>
            <div class="flex mt-5 justify-end">
               <a href={`/project/${projectId}/add-client`}>
                <Button class="w-fit" color="dark">Add Client</Button> 
            </a>
            </div>
            <div class="mt-4 pb-8 min-h-screen">
                <TableSearch divClass="font-sans" placeholder="Search by client name" hoverable={true} bind:inputValue={searchTerm}>
                   <Table shadow>
                        <TableHead defaultRow={false} theadClass="border-black">
                            <tr class="bg-primary-100">
                                <TableHeadCell class="text-white font-serif">Name</TableHeadCell>
                                <TableHeadCell class="text-white font-serif">Type</TableHeadCell>
                                <TableHeadCell class="text-white font-serif">Committed Amount</TableHeadCell>
                                <TableHeadCell class="text-white font-serif">Invested Amount</TableHeadCell>
                                <TableHeadCell class="text-white font-serif">Action</TableHeadCell>
                            </tr>
                        </TableHead>
                        <TableBody>
                            {#each filteredItems as client}
                                <TableBodyRow>
                                    <TableBodyCell class="font-sans">{client.name}</TableBodyCell>
                                    <TableBodyCell class="font-sans">{client.type}</TableBodyCell>
                                    <TableBodyCell class="font-sans">{numberWithCommas(client.committedAmount)}</TableBodyCell>
                                    <TableBodyCell class="font-sans">{numberWithCommas(client.investedAmount)}</TableBodyCell>
                                    <TableBodyCell class="flex gap-8 font-sans"><a on:click={()=> {
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
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="flex mt-5 justify-end">
                <a href={`/project/${projectId}/add-employee`}>
                    <Button class="w-fit" color="dark">Add Employee</Button> 
                </a>
                </div>
                <div class="mt-4 pb-8 min-h-screen">
                    <TableSearch placeholder="Search by employee name" divClass="font-sans" hoverable={true} bind:inputValue={searchTerm}>
                    <Table shadow>
                            <TableHead defaultRow={false} theadClass="border-black">
                                <tr class="bg-primary-100">
                                    <TableHeadCell class="text-white font-serif">Name</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Employee Type</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Wage Type</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Wage</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Action</TableHeadCell>
                                </tr>
                            </TableHead>
                            <TableBody>
                                {#each filteredEmployees as employee}
                                    <TableBodyRow>
                                        <TableBodyCell class="font-sans">{employee.name}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{employee.employeeType}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{employee.wageType}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{numberWithCommas(employee.wage)}</TableBodyCell>
                                        <TableBodyCell class="font-sans">
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
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-5">
                    <p class="text-sm font-serif">Project Code</p>
                    <div class="mt-2 border-black bg-primary-50 h-16 rounded p-4 flex justify-between items-center">
                        <p class="font-sans text-base text-white">{projectCode}</p>
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <svg on:click={copyProjectCode} class="w-5 h-5 fill-white hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,21H8V7H19M19,5H8A2,2 0 0,0 6,7V21A2,2 0 0,0 8,23H19A2,2 0 0,0 21,21V7A2,2 0 0,0 19,5M16,1H4A2,2 0 0,0 2,3V17H4V3H16V1Z" /></svg>
                    </div>
                </div>
                <div class="mt-4">
                    <p class="text-sm font-serif">Change project status</p>
                    <div class="mt-2 border-black bg-white h-16 rounded p-4 flex justify-between items-center shadow-md shadow-primary-600">
                        <p class="font-sans text-base">{projectStatus}</p>
                        {#if chosenStatus.toUpperCase() !== projectStatus}
                            <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                            <div class="flex gap-5 items-center">
                                <select bind:value={chosenStatus} class="rounded w-60 font-sans">
                                    {#if projectStatus === "ONGOING"}
                                        <option value="complete">Complete</option>
                                    {:else}
                                        <option value="ongoing">Ongoing</option>
                                    {/if}
                                </select>
                                <!-- svelte-ignore a11y-click-events-have-key-events -->
                                <!-- svelte-ignore missing-declaration -->
                                <p on:click={changeStatus} class="text-lg font-sans hover:cursor-pointer hover:text-primary-200">save</p>
                            </div>
                        {:else}
                            <select bind:value={chosenStatus} class="rounded w-60 font-sans">
                                {#if projectStatus === "ONGOING"}
                                    <option value="complete">Complete</option>
                                {:else}
                                    <option value="ongoing">Ongoing</option>
                                {/if}
                            </select>
                        {/if}
                        
                    </div>
                </div>
            </div>    
        {:else if active === 'tasks'}    
            <div class="container">
                <div class="flex justify-between h-8 align-middle text-base">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-2 flex justify-end">
                    <a href={`/project/${projectId}/add-task`}>
                        <Button class="w-fit" color="dark">Add Task</Button> 
                    </a>
                </div>
                
                        <div class="flex justify-center gap-20 items-center">
                            {#if taskSplit === 'unassigned'}
                                <p class="font-serif underline text-lg text-primary-200">Unassigned</p>
                                <!-- svelte-ignore a11y-click-events-have-key-events -->
                                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                                <p on:click={()=> taskSplit = 'assigned'} class="font-serif underline hover:cursor-pointer hover:text-primary-200">Assigned</p>
                            {:else}
                                <!-- svelte-ignore a11y-click-events-have-key-events -->
                                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                                <p on:click={()=> taskSplit = 'unassigned'} class="font-serif underline hover:cursor-pointer hover:text-primary-200">Unassigned</p>
                                <p class="font-serif underline text-lg text-primary-200">Assigned</p>
                                <!-- svelte-ignore a11y-no-static-element-interactions -->
                                <div class="flex items-center gap-4">
                                    {#if !view}
                                        <p class="font-sans text-lg">Track view</p>
                                    {:else}
                                        <p class="font-sans text-lg">Table view</p> 
                                    {/if}
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <svg on:click={()=> view = !view} class="h-6 w-6 hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21,9L17,5V8H10V10H17V13M7,11L3,15L7,19V16H14V14H7V11Z" /></svg>
                                </div>
                            {/if}    
                        </div>


                {#if taskSplit === 'unassigned'}
                    <div class="mt-4 pb-8 min-h-screen">
                        <TableSearch placeholder="Search by title" divClass="font-sans" hoverable={true} bind:inputValue={unassignedTaskSearchTerm}>
                        <Table  shadow>
                                <TableHead defaultRow={false} theadClass="border-black">
                                    <tr class="bg-primary-100">
                                        <TableHeadCell class="text-white font-serif">Title</TableHeadCell>
                                        <TableHeadCell class="text-white font-serif">Description</TableHeadCell>
                                        <TableHeadCell class="text-white font-serif">Priority</TableHeadCell>
                                        <TableHeadCell class="text-white font-serif">Status</TableHeadCell>
                                        <TableHeadCell class="text-white font-serif">Actions</TableHeadCell>
                                    </tr>
                                </TableHead>
                                <TableBody>
                                    {#each filteredUnassignedTasks as task}
                                        <TableBodyRow>
                                            <TableBodyCell class="font-sans">{task.title}</TableBodyCell>
                                            <TableBodyCell class="font-sans">{task.description}</TableBodyCell>
                                            <TableBodyCell class="font-sans">{task.priority}</TableBodyCell>
                                            <TableBodyCell class="font-sans">{task.status}</TableBodyCell>
                                            <TableBodyCell class="font-sans">
                                                <div class="flex gap-4 items-center">
                                                    <a class="underline hover:cursor-pointer hover:text-primary-200"  href={`/project/${projectId}/${task.taskId}/assign`}>assign</a>
                                                </div>  
                                            </TableBodyCell>
                                        </TableBodyRow>
                                    {/each}
                                </TableBody>
                        </Table> 
                        </TableSearch>
                    </div>
                {:else}

                {#if !view}
                <div class="mt-5 min-h-screen grid grid-cols-3 w-full">
                    {#if bigLoading}
                        <div class="mt-20">
                            <Loader />
                        </div>
                    {:else}
                    <div class="border-l border-t border-r w-full border-primary-100 flex flex-col items-center pt-5 pl-16 pr-16 bg-blue-50">
                        <div class="text-base mb-4">TODO</div>
                        {#each toDoTasks as task }
                            <div class="w-full h-48 rounded-md shadow flex border flex-col justify-between bg-primary-50 pt-4 pl-4 pb-4 pr-4 mb-4">
                                <p class="text-white text-base overflow-hidden">{task.title}</p>
                                {#if task.priority === "medium"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,13H5V11H19V13Z" /></svg>
                                {:else if task.priority === "low"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" /></svg>
                                {:else}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,15.41L12,10.83L16.59,15.41L18,14L12,8L6,14L7.41,15.41Z" /></svg>
                                {/if}
                                <div class="flex justify-between items-center">
                                    <a href={`/task/${task.taskId}`}>
                                        <p class="text-white underline hover:cursor-pointer font-serif hover:text-primary-200">View</p>
                                     </a>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                    <svg on:click={()=> moveTask(task.taskId, "in_progress")} class="w-8 h-8 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z" /></svg> 
                                </div>  
                            </div>
                        {/each}  
                    </div>
                    <div class="border-l border-t border-r border-primary-100 flex flex-col items-center pt-5 pl-16 pr-16 bg-blue-50">
                        <div class="text-base mb-4">IN PROGRESS</div> 
                        {#each inProgressTasks as task }
                        <div class="w-full h-48 rounded-md shadow flex border flex-col justify-between bg-primary-50 pt-4 pl-4 pb-4 pr-4 mb-4">
                                <p class="text-white text-base overflow-hidden">{task.title}</p>
                                {#if task.priority === "medium"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,13H5V11H19V13Z" /></svg>
                                {:else if task.priority === "low"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" /></svg>
                                {:else}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,15.41L12,10.83L16.59,15.41L18,14L12,8L6,14L7.41,15.41Z" /></svg>
                                {/if}
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <div class="flex justify-between items-center">
                                        <a href={`/task/${task.taskId}`}>
                                            <p class="text-white underline hover:cursor-pointer font-serif hover:text-primary-200">View</p>
                                         </a>
                                         <div class="flex gap-0">
                                            <!-- svelte-ignore a11y-click-events-have-key-events -->
                                             <!-- svelte-ignore a11y-no-static-element-interactions -->
                                            <svg on:click={()=> moveTask(task.taskId, "todo")} class="w-8 h-8 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z" /></svg>
                                            <!-- svelte-ignore a11y-click-events-have-key-events -->
                                            <!-- svelte-ignore a11y-no-static-element-interactions -->
                                            <svg on:click={()=> moveTask(task.taskId, "done")} class="w-8 h-8 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z" /></svg>   
                                        </div>   
                                    </div>            
                            </div> 
                        {/each} 
                    </div>
                    <div class="border-l border-t border-r border-primary-100 flex flex-col items-center pt-5 pl-16 pr-16 bg-blue-50">
                        <div class="text-base mb-4">DONE</div>
                        {#each doneTasks as task}
                        <div class="w-full h-48 rounded-md shadow flex border flex-col justify-between bg-primary-50 pt-4 pl-4 pb-4 pr-4 mb-4">
                                <p class="text-white text-base overflow-hidden">{task.title}</p>
                                <!-- svelte-ignore a11y-no-static-element-interactions -->
                                {#if task.priority === "medium"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,13H5V11H19V13Z" /></svg>
                                {:else if task.priority === "low"}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" /></svg>
                                {:else}
                                    <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M7.41,15.41L12,10.83L16.59,15.41L18,14L12,8L6,14L7.41,15.41Z" /></svg>
                                {/if}
                                <div class="flex justify-between items-center">
                                    <a href={`/task/${task.taskId}`}>
                                        <p class="text-white underline hover:cursor-pointer font-serif hover:text-primary-200">View</p>
                                     </a>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                    <svg on:click={()=> moveTask(task.taskId, "in_progress")} class="w-8 h-8 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z" /></svg>
                            </div> 
                        </div> 
                        {/each}  
                    </div>
                    {/if}    
                </div> 
                {:else}
                <div class="mt-5">
                    <TableSearch placeholder="Search by task name" divClass="font-sans" hoverable={true} bind:inputValue={taskSearchTerm}>
                        <Table shadow>
                             <TableHead defaultRow={false} theadClass="border-black">
                                 <tr class="bg-primary-100">
                                     <TableHeadCell class="text-white font-serif">Name</TableHeadCell>
                                     <TableHeadCell on:click={sortByPriority} class="text-white font-serif hover:cursor-pointer">Priority</TableHeadCell>
                                     <TableHeadCell on:click={sortByStatus} class="text-white font-serif hover:cursor-pointer">Status</TableHeadCell>
                                     <TableHeadCell class="text-white font-serif">Actions</TableHeadCell>
                                 </tr>
                             </TableHead>
                             <TableBody>
                                 {#each filteredTasks as task}
                                     <TableBodyRow>
                                         <TableBodyCell class="font-sans">{task.title}</TableBodyCell>
                                         <TableBodyCell class="font-sans">{task.priority}</TableBodyCell>
                                         <TableBodyCell class="font-sans">{task.status}</TableBodyCell>
                                         <TableBodyCell class="flex gap-10 font-sans">
                                                <a class="underline hover:cursor-pointer font-serif hover:text-primary-200" href={`/task/${task.taskId}`}>view</a>
                                                {#if task.status.toLowerCase() === 'todo'}
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "done")} class="underline hover:cursor-pointer font-serif hover:text-primary-200">move to done</a>  
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "in_progress")} class="underline hover:cursor-pointer font-serif hover:text-primary-200" >move to in progress</a>     
                                                {:else if task.status.toLowerCase() === 'in_progress'}
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "todo")} class="underline hover:cursor-pointer font-serif hover:text-primary-200" >move to todo</a> 
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "done")} class="underline hover:cursor-pointer font-serif hover:text-primary-200" >move to done</a> 
                                                {:else}
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "todo")} class="underline hover:cursor-pointer font-serif hover:text-primary-200" >move to todo</a> 
                                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                                    <!-- svelte-ignore a11y-missing-attribute -->
                                                    <a on:click={()=> moveTask(task.taskId, "in_progress")} class="underline hover:cursor-pointer font-serif hover:text-primary-200">move to in progress</a>   
                                                {/if}               
                                             </TableBodyCell>
                                     </TableBodyRow>
                                 {/each}
                             </TableBody>
                        </Table> 
                     </TableSearch>
                </div>
                {/if}
                {/if}
            </div>
        {:else if active === 'reports'}     
            <div class="container">
                <div class="flex justify-between h-8 align-middle text-base">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-5">
                    <TableSearch placeholder="Search by report name" divClass="font-sans" hoverable={true} bind:inputValue={reportSearchTerm}>
                        <Table  shadow>
                             <TableHead defaultRow={false} theadClass="border-black">
                                 <tr class="bg-primary-100">
                                     <TableHeadCell class="text-white font-serif">#</TableHeadCell>
                                     <TableHeadCell class="text-white font-serif">Name</TableHeadCell>
                                     <TableHeadCell class="text-white font-serif">Action</TableHeadCell>
                                 </tr>
                             </TableHead>
                             <TableBody>
                                 {#each filteredReports as report}
                                     <TableBodyRow>
                                         <TableBodyCell class="font-sans">{report.id}</TableBodyCell>
                                         <TableBodyCell class="font-sans">{report.name}</TableBodyCell>
                                         <TableBodyCell class="flex gap-8 font-sans">
                                            {#if report.id === 1}
                                                <a target="_blank" class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/report/general`}>View</a>
                                            {:else if report.id === 2}
                                                <a target="_blank" class="underline hover:cursor-pointer hover:text-primary-200" href={`/project/${projectId}/report/client`}>View</a>
                                            {/if}                
                                             </TableBodyCell>
                                     </TableBodyRow>
                                 {/each}
                             </TableBody>
                        </Table> 
                     </TableSearch>
                </div>
            </div>  
        {:else if active === 'expenses'}
            <div class="container">
                <div class="flex justify-between h-8 align-middle text-base">
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("overview")}>Overview</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("clients")}>Clients</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("employees")}>Employees</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("tasks")}>Tasks</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("expenses")}>Expenses</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("reports")}>Reports</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="font-serif underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("actions")}>Actions</p>
                </div>
                <div class="mt-2 flex justify-end">
                    <a href={`/project/${projectId}/add-expense`}>
                        <Button class="w-fit" color="dark">Add Expense</Button> 
                    </a>
                </div>
                <div class="mt-4">
                    <TableSearch placeholder="Search by title" divClass="font-sans" hoverable={true} bind:inputValue={expenseSearchTerm}>
                        <Table  shadow>
                            <TableHead defaultRow={false} theadClass="border-black">
                                <tr class="bg-primary-100">
                                    <TableHeadCell class="text-white font-serif">Title</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Expense Type</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Cost</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Added By</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Date</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Document</TableHeadCell>
                                    <TableHeadCell class="text-white font-serif">Note</TableHeadCell>
                                </tr>
                            </TableHead>
                            <TableBody>
                                {#each filteredExpenses as expense}
                                    <TableBodyRow>
                                        <TableBodyCell class="font-sans">{expense.title}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{expense.type}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{numberWithCommas(expense.cost)}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{expense.addedBy}</TableBodyCell>
                                        <TableBodyCell class="font-sans">{expense.date}</TableBodyCell>
                                        <TableBodyCell class="font-sans">
                                            {#if expense.hasDocument === true}
                                                <div class="flex gap-4 items-center">
                                                    <a class="underline hover:cursor-pointer hover:text-primary-200" href={`/expense/document/${expense.expenseId}`}>View</a>
                                                </div>
                                            {:else}
                                                <p>n/a</p>
                                            {/if}     
                                        </TableBodyCell>
                                        <TableBodyCell class="font-sans">{expense.note}</TableBodyCell>
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
</style>