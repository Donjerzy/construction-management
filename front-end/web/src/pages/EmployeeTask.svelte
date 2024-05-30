<script>
    import { onMount } from "svelte";
    import {firstName, accessToken, loggedIn, projectUUID} from '../stores.js' 
    import { get } from "svelte/store";
    import EmployeeComponent from "../components/employee-component.svelte";
    import Toast from "../components/toast.svelte";
    import { notifications } from "../lib/notification.js";
    let contentTitle = 'Tasks';
    let toDoTasks = [];
    let inProgressTasks = [];
    let doneTasks = [];


    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/task/all?projectId=${get(projectUUID)}`, {
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
            },
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
                toDoTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'todo');
                inProgressTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'in_progress');
                doneTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'done');
            }
        });
    });

    async function refetchTasks() {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/task/all?projectId=${get(projectUUID)}`, {
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
            },
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
                toDoTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'todo');
                inProgressTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'in_progress');
                doneTasks = result.tasks.filter((task)=> task.status.toLowerCase() === 'done');
            }
        });
    }


    async function moveTask(taskId,to) {
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/employee/task/move', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Cmt': `CMT ${get(accessToken)}`
            },
            body: JSON.stringify({
                taskId: taskId,
                action: to 
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
                refetchTasks();
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

</script>



<EmployeeComponent contentTitle={contentTitle}>
    <Toast />
    <div class="mt-5 min-h-[580px] grid grid-cols-3 w-full">

        <div class="border-l border-t border-r border-primary-100 flex flex-col items-center pt-5 pl-16 pr-16 bg-blue-50">
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
                            <a href={`/employee/task/${task.taskId}`}>
                                <p class="underline text-white hover:cursor-pointer hover:text-primary-200">View</p> 
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
                        <div class="flex justify-between items-center">
                            <a href={`/employee/task/${task.taskId}`}>
                                <p class="underline text-white hover:cursor-pointer hover:text-primary-200">View</p> 
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
            {#each doneTasks as task }
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
                            <a href={`/employee/task/${task.taskId}`}>
                                <p class="underline text-white hover:cursor-pointer hover:text-primary-200">View</p> 
                            </a>
                            <!-- svelte-ignore a11y-click-events-have-key-events -->
                            <!-- svelte-ignore a11y-no-static-element-interactions -->
                            <svg on:click={()=> moveTask(task.taskId, "in_progress")} class="w-8 h-8 fill-white hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z" /></svg>
                        </div>                 
                </div>
            {/each} 
        </div>

    </div>
</EmployeeComponent>