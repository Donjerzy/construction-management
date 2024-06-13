<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'
    import { get } from "svelte/store";
    import { Button } from 'flowbite-svelte';
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    import { onMount } from "svelte";
    import { Modal } from 'flowbite-svelte';
    


    const projectId = $page.params.projectId;
    let contentTitle = "Add Task";
    let loading = false;
    let appName = "Mjengo Bora Construction";
    let title = "";
    let description = "";
    let employeeList = [];
    let chosenEmployees = new Set();
    let chosenEmployeeNames = new Set();
    let chosenEmployeeNamesDisplay = [];
    let defaultModal = false;
    let employeeTypes = [];
    let chosenType;
    let suggestedLoading = false;
    let suggestedEmployees = [];
    let priority;

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

    async function addTask() {
        if(title === undefined || title.length === 0) {
           return notifications.danger("Invalid title", 1000);
        }
        if (priority === undefined || priority === "0") {
            return notifications.danger("Priority is a required field", 1000);
        }

        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/task/add-task', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify({
                title: title,
                description: description,
                project: projectId,
                employees: Array.from(chosenEmployees),
                priority: priority
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
                notifications.success("Task added successfully", 1000);
                window.location.replace(`/project/${projectId}`);
                return;
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger("Error encountered while creating task", 1000); 
        }
        if(error) {
            firstName.set("");
            accessToken.set("");
            loggedIn.set("false");
            window.location.replace('/'); 
        }
    }


</script>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <Toast />
    <div class="mt-4">
        <form>
            <div class="flex gap-20 items-center w-full mt-5">
                <div class="flex flex-col gap-2 w-full">
                    <label class="font-serif text-sm" for="title">Title</label>
                    <input name="name" class="font-sans rounded h-10 w-full border-primary-800" type="text" id="title" bind:value={title}>
                </div>
                <div class="flex flex-col gap-2 w-full">
                    <label class="font-serif text-sm" for="title">Priority</label>
                    <select class="font-serif w-full" id="priority" bind:value={priority}>
                        <option value="0">--Select--</option>
                        <option value="low">Low</option>
                        <option value="medium">Medium</option>
                        <option value="high">High</option>
                    </select>
                </div>
            </div>
            <div class="flex flex-col gap-2 mt-5">
                <label class="font-serif text-sm" for="description">Description</label>
                <textarea class="font-sans" bind:value={description} id="description"></textarea>
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

           

            <div class="mt-4 flex justify-between items-center">
                {#if loading}
                    <Loader />
                {:else}
                    <Button on:click={addTask} class="w-fit" color="dark">Add Task</Button> 
                    <button on:click={()=> defaultModal = true} class="bg-primary-500 h-10 w-10 flex justify-center items-center rounded-3xl hover:bg-primary-200 hover:cursor-pointer">
                        <svg class="w-5 h-5 fill-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12,2A7,7 0 0,0 5,9C5,11.38 6.19,13.47 8,14.74V17A1,1 0 0,0 9,18H15A1,1 0 0,0 16,17V14.74C17.81,13.47 19,11.38 19,9A7,7 0 0,0 12,2M9,21A1,1 0 0,0 10,22H14A1,1 0 0,0 15,21V20H9V21Z" /></svg>
                    </button>
                {/if} 
            </div>
        </form>

        <Modal title="Suggested employees" bind:open={defaultModal}>
            <p class="text-base leading-relaxed text-gray-500 dark:text-gray-400 font-serif">Generate a list of suitable task employees based on their active tasks and their average task completion time. The generated list is ordered from most suitable to least suitable.</p>
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

    </div>
</AdminComponent>