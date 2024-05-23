<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    let contentTitle = "Task";
    import {firstName, accessToken, loggedIn, projectClient} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import { notifications } from "../lib/notification";
    import Loader from "../components/loading-component.svelte";
    import {page} from '$app/stores';
    let current = "view" // view || comments ||
    let loading = false;

    const taskId = $page.params.taskId;
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
    

    let userComment = "";

    function navigate(to) {
        current = to;
    }

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/task/view?taskId=${taskId}`, {
            headers: {
                'Cmt': `CMT ${get(accessToken)}`
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
    })


    async function refetchTask() {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/task/view?taskId=${taskId}`, {
        headers: {
            'Cmt': `CMT ${get(accessToken)}`
        }
        })
        .then(response => {
            if(!response.ok) {
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
                loading = false;
            }
        }) 
    }

    
    async function addComment() {
        if(userComment.length === 0) {
            return notifications.danger("Comment cannot be empty", 1000);
        }
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/employee/task/add-comment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Cmt': `CMT ${get(accessToken)}`
            },
            body: JSON.stringify({
                taskId: taskId,
                comment: userComment
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
                userComment = "";
                refetchTask();
                return;
            }
        }).catch(error=> {
            loading = false;
            notifications.danger("Could make request to server", 1000)
        })
        if(existsError) {
            notifications.danger("Error", 1000); 
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
    <div class="mt-5 h-[80%]">
        {#if current === "view"}
            <div class="flex gap-16 h-8 align-middle text-base w-fit ml-auto mr-auto">
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-200 text-lg hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("view")}>View</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("comments")}>Comments</p>
            </div> 

            <div class="mt-6 flex gap-4  h-full">
                <div class="flex gap-7 border border-primary-100 rounded-md  p-4 bg-white shadow-xl w-full h-full">
                    <div>
                       <svg class="w-10 h-10 mx-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M14,10H19.5L14,4.5V10M5,3H15L21,9V19A2,2 0 0,1 19,21H5C3.89,21 3,20.1 3,19V5C3,3.89 3.89,3 5,3M5,5V19H19V12H12V5H5Z" /></svg>
                    </div>
                    <div class="flex flex-col gap-1 overflow-auto w-full h-full">
                        <p class="underline text-lg">Task Information</p>
                        <div class="mt-2">
                            <div class="flex items-center justify-between pr-4 border-b pb-2">
                                <p class="text-base">Title:</p>
                                <p class="italic text-sm">{task.title}</p>
                            </div>
                            <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                                <p class="text-base">Description:</p>
                                <p class="italic text-sm">{task.description}</p>
                            </div>
                            <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                                <p class="text-base">Status:</p>
                                <p class="italic text-sm">{task.status}</p>
                            </div>
                            <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                                <p class="text-base">Creation Date:</p>
                                <p class="italic text-sm">{task.creationDate}</p>
                            </div>
                            <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                                <p class="text-base">Completion Date:</p>
                                <p class="italic text-sm">{task.completionDate}</p>
                            </div>
                            <div class="pb-2 pt-2">
                                <p class="text-base ml-auto mr-auto w-fit">Assigned Employees</p>
                                <div class="border border-primary-100 min-w-[600px] mt-2 min-h-[100px] rounded flex felx-wrap p-3 bg-purple-50">
                                    {#each Object.values(task.employees) as employee}
                                       <div class="flex items-center h-12 p-3 gap-2 mr-4 rounded shadow-md bg-white">
                                           <p>{employee}</p>
                                       </div> 
                                    {/each}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        {:else if current === "comments"}
            <div class="flex gap-16 h-8 align-middle text-base w-fit ml-auto mr-auto">
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("view")}>View</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-200 text-lg  hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("comments")}>Comments</p>
            </div> 
            <div class="mt-6 h-4/5 border border-primary-100 rounded-md bg-white overflow-auto pt-4 pr-4 pl-4">
                {#each task.taskComments as comment}
                    {#if comment.commenter.toLowerCase() === 'you'}
                        <div class="flex flex-col gap-1 items-end ml-1 mb-4">
                            <p class="text-sm">{comment.commenter}</p>
                            <p class="border bg-purple-200 border-purple-400 rounded-br-md rounded-tl-md p-4 text-base">{comment.comment}</p>
                        </div>
                    {:else}
                        <div class="flex flex-col gap-1 items-start ml-1 mb-4">
                            <p class="text-sm">{comment.commenter}</p>
                            <p class="border bg-purple-200 border-purple-400 rounded-bl-md rounded-tr-md p-4 text-base">{comment.comment}</p>
                        </div>
                    {/if}
                    
                {/each}
            </div>
            <!-- svelte-ignore a11y-no-static-element-interactions -->
            <div class="flex items-center h-20  justify-between mt-2 gap-4">
                <input  class="font-sans rounded h-14 w-full" type="text" bind:value={userComment}>
                {#if loading}
                    <Loader />
                {:else}
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <svg on:click={addComment} class="h-10 w-10 hover:cursor-pointer hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M2,21L23,12L2,3V10L17,12L2,14V21Z" /></svg>
                {/if}
                 </div>
        {/if}
        
    </div>
</EmployeeComponent>

