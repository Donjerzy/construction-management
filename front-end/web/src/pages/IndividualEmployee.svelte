<script>
    export let projectId;
    export let employeeId;
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import { onMount } from "svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import Loader from "../components/loading-component.svelte";
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Employee"
    let employee = {
        "id": 204,
        "name": "Jane Doe",
        "employeeType": "Painter",
        "email": "jane.doe@gmail.com",
        "tasksCompleted": 0,
        "tasksCompletedOnTime": 0,
        "tasksCompletedPastTime": 0,
        "tasksOngoing": 0,
        "wage": "20000",
        "wageType": "Monthly",
        "archetype": "n/a",
        "totalTasks": 0,
        "joinDate": "2024-02-25",
        "hasContract": "yess",
        "wagesPaid": "500"
    }
    let contractChosen  = false;
    let contract = null;
    let loading = false;

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/project/get-employee?project=${projectId}&employee=${employeeId}`, {
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
                employee = result.employee;
            }
        })
    })



    async function saveContract(employee) {
        loading = true;
        let error = false;
        let existsError = false;
        let errorMessage = "";
        let data = new FormData();
        data.append('employeeId', employee.employeeId);
        data.append('contract', employee.contract)

        await fetch('http://localhost:8080/api/v1/employee/add-contract', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${get(accessToken)}`
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
                notifications.success("Contract added successfully", 1000);
                window.location.reload();
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



    function handleContractUpload(event) {
        contract = event.target.files[0];
        contractChosen = true;
    }

    function removeContract() {
        contract = null;
        contractChosen = false;
    }
</script>


<svelte:head>
    <title>Employee</title>
</svelte:head>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <Toast />
    <div class="mt-4 min-h-4 rounded-md flex gap-8 items-center">
        <Button 
            height=10 width=36 label="Edit" fontSize="sm" padding="8px"
        />
        <Button 
            height=10 width=36 label="Reset Password" fontSize="sm" padding="8px"
        />
        <Button 
            height=10 width=36 label="Pay Wages" fontSize="sm" padding="8px"
        />
    </div>
    <div class="mt-6 flex gap-4">
        <!-- Card -->
        <div class="flex gap-7 border  border-primary-100 rounded-md w-fit p-4 bg-white shadow-xl">
            <div>
                <svg class="w-10 h-10 mx-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M2,3H22C23.05,3 24,3.95 24,5V19C24,20.05 23.05,21 22,21H2C0.95,21 0,20.05 0,19V5C0,3.95 0.95,3 2,3M14,6V7H22V6H14M14,8V9H21.5L22,9V8H14M14,10V11H21V10H14M8,13.91C6,13.91 2,15 2,17V18H14V17C14,15 10,13.91 8,13.91M8,6A3,3 0 0,0 5,9A3,3 0 0,0 8,12A3,3 0 0,0 11,9A3,3 0 0,0 8,6Z" /></svg>
            </div>
            <div class="flex flex-col w-[500px]">
                <p class="underline text-lg">Personal Information</p>
                <div class="mt-2">
                    <div class="flex flex-col gap-1 max-h-72 overflow-auto w-[500px]">
                        <div class="flex items-center justify-between pr-4 border-b pb-2">
                            <p class="text-base">Name:</p>
                            <p class="italic text-sm">{employee.name}</p>
                        </div>
                        <div class="flex items-center justify-between pr-4 border-b pb-2">
                            <p class="text-base">Email:</p>
                            <p class="italic text-sm">{employee.email}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="flex gap-7 border border-primary-100 rounded-md w-fit p-4 bg-white shadow-xl">
            <div>
                <svg class="w-10 h-10 mx-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M13 3C9.23 3 6.19 5.95 6 9.66L4.08 12.19C3.84 12.5 4.08 13 4.5 13H6V16C6 17.11 6.89 18 8 18H9V21H16V16.31C18.37 15.19 20 12.8 20 10C20 6.14 16.88 3 13 3M17.06 9.57L15.1 10.09L16.54 11.54C16.89 11.88 16.89 12.46 16.54 12.81C16.19 13.16 15.61 13.16 15.27 12.81L13.81 11.37L13.3 13.33C13.18 13.82 12.68 14.1 12.21 13.97C11.72 13.84 11.44 13.35 11.57 12.87L12.1 10.9L10.13 11.43C9.65 11.56 9.15 11.28 9.03 10.79C8.9 10.32 9.18 9.82 9.67 9.7L11.63 9.19L10.19 7.73C9.84 7.39 9.84 6.82 10.19 6.46C10.54 6.11 11.12 6.11 11.46 6.46L12.91 7.9L13.43 5.94C13.55 5.46 14.04 5.18 14.5 5.3C15 5.43 15.28 5.92 15.16 6.41L14.63 8.37L16.59 7.84C17.08 7.72 17.57 8 17.7 8.5C17.82 8.96 17.54 9.45 17.06 9.57Z" /></svg>
            </div>
            <div class="flex flex-col">
                <p class="underline text-lg">Other Information</p>
                <div class="mt-2">
                    <div class="flex flex-col gap-1 max-h-72 overflow-auto w-[340px]">
                        <div class="flex items-center justify-between pr-4 border-b pb-2">
                            <p class="text-base">Archetype:</p>
                            <p class="italic text-sm">{employee.archetype}</p>
                        </div>  
                    </div>
                </div>  
            </div>
        </div>
    </div>
    <div class="mt-6 flex gap-4">
        <div class="flex gap-7 border border-primary-100 rounded-md w-fit p-4 bg-white shadow-xl">
            <div>
                <svg class="w-10 h-10 mx-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M10,2H14A2,2 0 0,1 16,4V6H20A2,2 0 0,1 22,8V19A2,2 0 0,1 20,21H4C2.89,21 2,20.1 2,19V8C2,6.89 2.89,6 4,6H8V4C8,2.89 8.89,2 10,2M14,6V4H10V6H14Z" /></svg>
            </div>
            <div class="flex flex-col gap-1 max-h-72 overflow-auto w-[500px]">
                <p class="underline text-lg">Employment Information</p>
                <div class="mt-2">
                    <div class="flex items-center justify-between pr-4 border-b pb-2">
                        <p class="text-base">Employee Type:</p>
                        <p class="italic text-sm">{employee.employeeType}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Join Date:</p>
                        <p class="italic text-sm">{employee.joinDate}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Tasks:</p>
                        <p class="italic text-sm">{employee.totalTasks}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Completed Tasks:</p>
                        <p class="italic text-sm">{employee.tasksCompleted}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Completed On Time Tasks:</p>
                        <p class="italic text-sm">{employee.tasksCompletedOnTime}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Completed Past Time:</p>
                        <p class="italic text-sm">{employee.tasksCompletedPastTime}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Ongoing Tasks:</p>
                        <p class="italic text-sm">{employee.tasksOngoing}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Wage Type:</p>
                        <p class="italic text-sm">{employee.wageType}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Wage:</p>
                        <p class="italic text-sm">{employee.wage}</p>
                    </div>
                    <div class="flex items-center justify-between pr-4 border-b pb-2 pt-2">
                        <p class="text-base">Wage Paid:</p>
                        <p class="italic text-sm">{employee.wagesPaid}</p>
                    </div>
                </div>
            </div>
        </div>
        <!--Contract Card-->
        <div class="flex gap-7 border border-primary-100 rounded-md w-fit p-4 bg-white shadow-xl">
            <div>
                <svg class="w-10 h-10 mx-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M6,2A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2H6M6,4H13V9H18V20H6V4M8,12V14H16V12H8M8,16V18H13V16H8Z" /></svg>
            </div>
            <div class="flex flex-col">
                <p class="underline text-lg">Contract</p>
                <div class="flex justify-start w-[340px] max-h-full pt-4">
                    {#if employee.hasContract === 'yes'}
                        <div>
                            <Button height=10 width=36 label="View" fontSize="sm" padding="8px"
                            />
                        </div> 
                    {:else}
                        <div>
                            {#if contractChosen === false}
                                <input accept=".pdf" name="contract" class="rounded border-primary-800" type="file" id="contract" on:change={handleContractUpload}>
                            {:else}
                                <div class="flex gap-4 items-center">
                                    <p class="font-bold text-lg">File:</p>
                                    <p class="italic text-base">{contract.name}</p>
                                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                                    <!-- svelte-ignore a11y-no-static-element-interactions -->
                                    <svg on:click={()=> removeContract()} class="h-7 w-7 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21.12 22.54L19 20.41L16.88 22.54L15.47 21.12L17.59 19L15.47 16.88L16.88 15.47L19 17.59L21.12 15.47L22.54 16.88L20.41 19L22.54 21.12L21.12 22.54M14 2H6C4.89 2 4 2.89 4 4V20C4 21.11 4.89 22 6 22H13.81C13.28 21.09 13 20.05 13 19C13 15.69 15.69 13 19 13C19.34 13 19.67 13.03 20 13.08V8L14 2M13 9V3.5L18.5 9H13Z" /></svg>
                                </div>
                                <div class="mt-4">
                                    {#if loading}
                                        <Loader />
                                    {:else}    
                                        <Button on:click={()=> saveContract(
                                            {
                                                employeeId: employeeId,
                                                contract: contract
                                            }
                                        )} height=10 width=36 label="Save" fontSize="sm" padding="8px" />
                                    {/if} 
                                </div> 
                            {/if}    
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</AdminComponent>