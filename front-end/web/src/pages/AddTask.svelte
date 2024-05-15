<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    import { onMount } from "svelte";
    


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
                    name: employee.name, 
                    id: employee.id
                   }
                }
                );
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

    async function addTask() {
        if(title === undefined || title.length === 0) {
           return notifications.danger("Invalid title", 1000);
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
                employees: Array.from(chosenEmployees)
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

            <div class="flex flex-col gap-2 mt-1">
                <label for="title">Title</label>
                <input name="name" class="rounded border-primary-800" type="text" id="title" bind:value={title}>
            </div>
            <div class="flex flex-col gap-2 mt-1">
                <label for="description">Description</label>
                <textarea bind:value={description} id="description"></textarea>
            </div>

            <div class="flex gap-4 mt-2">
                <div class="flex flex-col">
                    <p>Chosen Employees</p>
                    <div class="border border-primary-100 min-w-[600px] mt-2 min-h-[200px] rounded flex felx-wrap p-3">
                        {#each chosenEmployeeNamesDisplay as employee}
                           <div class="flex items-center h-12 p-3 gap-2 mr-4 rounded shadow-md bg-white">
                               <p>{employee}</p>
                               <!-- svelte-ignore a11y-click-events-have-key-events -->
                               <!-- svelte-ignore a11y-no-static-element-interactions -->
                               <svg on:click={()=> removeChosenEmployee(employee)} class="h-5 w-5 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" /></svg>
                           </div> 
                        {/each}
                    </div>
                </div>
                <div class="flex flex-col">
                    <p>Choose employee</p>
                    <div class="mt-2">
                        <select class="hover:cursor-pointer" on:change={(e)=> addToChosenEmployees(e.target.value)}>
                            <option value="0">--Select--</option>
                            {#each employeeList as employee }
                                <option value={employee.id}>{employee.name}</option>
                            {/each}
                        </select>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                    height=12 width=36 label="Add Task" fontSize="sm" padding="8px"
                    on:click={addTask} />
                {/if} 
            </div>

        </form>

    </div>
</AdminComponent>