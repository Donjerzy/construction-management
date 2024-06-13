<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {page} from '$app/stores';
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import { onMount } from "svelte";
    import Loader from "../components/loading-component.svelte";
    import { Modal } from 'flowbite-svelte';
    import { Button } from 'flowbite-svelte';
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';

    const projectId = $page.params.projectId;
    const taskId = $page.params.taskId;

    let appName = "Mjengo Bora Construction";
    let contentTitle = "Assign Task";
    let suggestedLoading = false;
    let suggestedEmployees = [];
    let employeeList = [];
    let chosenEmployees = new Set();
    let chosenEmployeeNames = new Set();
    let chosenEmployeeNamesDisplay = [];
    let defaultModal = false;
    let loading = false;
    let chosenType;
    let employeeTypes = [];

    let task = {
        taskId: 0,
        title: "",
        creationDate: "",
        completionDate: "n/a",
        description: "",
        status: "",
        employees: {
            0: ""
        },
        taskHistory: [],
        taskComments: []
    };


    onMount(()=> {
        let errorFetch = false;
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
                employeeList = result.employees.map((employee)=> {
                   return { 
                    name: `${employee.name} - ${employee.employeeType}`, 
                    id: employee.id
                   }
                }
                );
            }
        })


        fetch(`http://localhost:8080/api/v1/employee-type/all`, {
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
                employeeTypes = result.employeeTypes;
                chosenType = result.employeeTypes[0].id
            }
        })


        fetch(`http://localhost:8080/api/v1/task/view?taskId=${taskId}`, {
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
                task = result.task;
            }
        })

    });


    function addToChosenEmployees(emp) {
        if(emp !== '0') {
            chosenEmployees.add(emp);
            let employee = "";
            for (const empItem of employeeList) {
                if (parseFloat(empItem.id) === parseFloat(emp)) {
                    employee = empItem.name;
                    break;
                }
            }
            chosenEmployeeNames.add(employee); 
        }
        chosenEmployeeNamesDisplay = Array.from(chosenEmployeeNames);
    }

    function removeChosenEmployee(name) {
        chosenEmployeeNamesDisplay = chosenEmployeeNamesDisplay.filter(function(item) {
            return item !== name
        });
        chosenEmployeeNames.delete(name);
        let nameIndex;
        for(let i=0 ; i < employeeList.length; i++ ) {
            if(employeeList[i].name === name) {
                nameIndex = `${employeeList[i].id}`;
                break;
            }
        }
        chosenEmployees.delete(nameIndex);
    }

    async function generateSuggestedList () {
        suggestedLoading = true;
        let error = false;
        let existsError = false;
        await fetch(`http://localhost:8080/api/v1/employee/suggested?empType=${chosenType}&project=${projectId}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            }
        }).then(response=> {
            suggestedLoading = false;
            if(response.status === 400) {
                existsError = true;
                suggestedEmployees = ["n/a"];
                return
            }
            if(!response.ok) {
                error = true;
                suggestedEmployees = ["n/a"];
                return
            } else {
                error = false;
                existsError = false;
                return response.json();;
            }
        }).then((result)=> {
            if(result.employees.length <= 0) {
                suggestedEmployees = ["n/a"];
            } else {
                suggestedEmployees = result.employees;
            }
        })
        .catch(error=> {
            loading = false;
            // notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            // notifications.danger("Error encountered", 1000); 
        }
    }

    async function assignToEmployees() {
        let assignedEmployees = Array.from(chosenEmployees);
        if (assignedEmployees.length <=0) {
            return notifications.danger("No employee has been selected", 1000);
        }

        let error = false;
        let existsError = false;
        loading = true;

        await fetch(`http://localhost:8080/api/v1/task/assign`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify(
                {
                    projectId: projectId,
                    taskId: taskId,
                    employees: assignedEmployees
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
                firstName.set("");
                accessToken.set("");
                loggedIn.set("false");
                window.location.replace('/');
                return
            } else {
                error = false;
                existsError = false;
                notifications.success("Task assigned successfully");
                window.location.replace(`/project/${projectId}`)
            }
        })
        .catch(error=> {
            loading = false;
            // notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            // notifications.danger("Error encountered", 1000); 
        }

    }

</script>



<AdminComponent appName = {appName} contentTitle={contentTitle} userFirstName={get(firstName)}>

    <Toast />

    <div class="flex gap-20 items-center w-full mt-5">
        <div class="flex-col gap-40 w-full">
            <p class="font-serif text-sm">Task</p>
            <p class="font-sans w-full h-10 italic border mt-1 border-primary-100 p-2 bg-primary-100 rounded">{task.title}</p>
        </div>
        <div class="flex-col gap-40 w-full">
            <p class="font-serif text-sm">Priority</p>
            <p class="font-sans w-full h-10 italic border mt-1 border-primary-100 p-2 bg-primary-100 rounded">{task.priority}</p>
        </div>
    </div>

    <div class="flex gap-4 mt-5">
        <div class="flex flex-col">
            <p class="font-serif text-sm">Chosen Employees</p>
            <div class="border border-primary-100 min-w-[600px] mt-2 min-h-[200px] rounded flex felx-wrap p-3">
                {#each chosenEmployeeNamesDisplay as employee}
                   <div class="flex items-center h-12 p-3 gap-2 mr-4 rounded shadow-md bg-white">
                       <p class="font-sans">{employee}</p>
                       <!-- svelte-ignore a11y-click-events-have-key-events -->
                       <!-- svelte-ignore a11y-no-static-element-interactions -->
                       <svg on:click={()=> removeChosenEmployee(employee)} class="h-5 w-5 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" /></svg>
                   </div> 
                {/each}
            </div>
        </div>
        <div class="flex flex-col w-full">
            <p class="font-serif text-sm">Choose employee</p>
            <div class="w-full mt-2">
                <select class="hover:cursor-pointer font-sans w-full" on:change={(e)=> addToChosenEmployees(e.target.value)}>
                    <option value="0">--Select--</option>
                    {#each employeeList as employee }
                        <option value={employee.id}>{employee.name}</option>
                    {/each}
                </select>
            </div>
        </div>
    </div>

    <div class="mt-5 flex justify-between items-center">
        {#if loading}
            <Loader />
        {:else}
             <Button on:click={assignToEmployees} class="w-fit" color="dark">Assign</Button> 
            <button on:click={()=> defaultModal = true} class="bg-primary-500 h-10 w-10 flex justify-center items-center rounded-3xl hover:bg-primary-200 hover:cursor-pointer">
                <svg class="w-5 h-5 fill-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12,2A7,7 0 0,0 5,9C5,11.38 6.19,13.47 8,14.74V17A1,1 0 0,0 9,18H15A1,1 0 0,0 16,17V14.74C17.81,13.47 19,11.38 19,9A7,7 0 0,0 12,2M9,21A1,1 0 0,0 10,22H14A1,1 0 0,0 15,21V20H9V21Z" /></svg>
            </button>
        {/if} 
    </div>


    <Modal title="Suggested employees" bind:open={defaultModal}>
        <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400 font-serif">
            Generate a list of suitable task employees based on their active tasks and their average task completion time. 
            The generated list is ordered from most suitable to least suitable.
        </p>
        <label for="et" class="mr-5">Employee Type</label>
        <select id="et" bind:value={chosenType}>
            {#each employeeTypes as type }
                    <option value={type.id}>{type.name}</option>
                {/each}
        </select>
        <div class="border bg-primary-800 border-primary-100 min-w-[600px] mt-2 min-h-[200px] rounded flex felx-wrap p-3">
            {#if suggestedLoading}
                <div class="flex w-full justify-center items-center">
                    <Loader />
                </div>
            {:else}  
                {#each suggestedEmployees as employee}
                   <div class="flex items-center h-12 p-3 gap-2 mr-4 rounded shadow-md bg-white">
                    <p class="font-sans">{employee}</p> 
                   </div> 
                {/each}          
            {/if}
        </div>
        <svelte:fragment slot="footer">
            <div class="flex justify-center w-full">
                <button on:click={generateSuggestedList} class="w-28 h-10 bg-primary-50 p-2 rounded text-white hover:bg-primary-200 font-serif">Generate</button>
            </div>
        </svelte:fragment>
      </Modal>

</AdminComponent>