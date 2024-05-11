<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import {onMount} from 'svelte';
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    const projectId = $page.params.projectId;
    let appName = "Mjengo Bora Construction";
    let title = "Add Employee";
    let loading = false;
    let userFirstName = "";
    let userLastName = "";
    let userEmail = "";
    let userPassword = "";
    let userWage;
    let userJoinDate;
    let userWageDisplay = '0';
    let currentPage = "one"  // one || two
    let employeeTypes = [];
    let chosenEmployeeType = 0;
    let wageTypes = [];
    let chosenWageType = 0;
    let contract = null;
    let contractChosen = false;


    function removeContract() {
        contract = null;
        contractChosen = false;
    }

    function handleContractUpload(event) {
        contract = event.target.files[0];
        contractChosen = true;
    }


    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    function updateUserWage(event) {
        userWage = event.target.value.replace(/,/g, '');
        userWageDisplay = formatWithCommas(userWage);
    }


    onMount(()=> {
        let errorFetch = false;
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
            }
        })

        fetch(`http://localhost:8080/api/v1/wage-type/all`, {
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
                wageTypes = result.wageTypes;
            }
        })
    })


    function hasOnlyLetters(inputString) {
        return /^[a-zA-Z\s]*$/.test(inputString);
    }

    async function AddEmployee(employee) {
        loading = true;
        let error = false;
        let existsError = false;
        let errorMessage = "";
        let data = new FormData();
        data.append('firstName', employee.firstName);
        data.append('lastName', employee.lastName);
        data.append('email', employee.email);    
        data.append('password', employee.password);  
        data.append('wage', employee.wage);  
        data.append('joinDate', employee.joinDate);
        data.append('employeeType', employee.employeeType);
        data.append('wageType', employee.wageType);
        data.append('project', employee.project);
        data.append('contract', employee.contract)

        await fetch('http://localhost:8080/api/v1/employee/add', {
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
                notifications.success("Employee added successfully", 1000);
                userFirstName = "";
                userLastName = "";
                userEmail = "";
                userPassword = "";
                userWageDisplay = "0";
                currentPage = "one"  // one || two
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

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }


    function validateInput(e) {
        e.preventDefault();
        let hasNoDigit = true;
        let hasNoUpper = true;
        let hasNoLower = true;
        if(userFirstName.length === 0) {
            notifications.danger("First name cannot be empty", 1000);
            return;
        }
        if(!hasOnlyLetters(userFirstName)) {
            notifications.danger("Invalid First Name", 1000);
            return;
        }
        if(userLastName.length === 0) {
            notifications.danger("Last name cannot be empty", 1000);
            return;
        }
        if(!hasOnlyLetters(userLastName)) {
            notifications.danger("Invalid Last Name", 1000);
            return;
        }
        if(userEmail.length === 0) {
            notifications.danger("Email cannot be empty", 1000);
            return;
        }
        if(!validateEmail(userEmail)) {
            notifications.danger("Invalid email address", 1000);
            return;
        }
        if(userPassword.length < 6) {
            notifications.danger('Password should have atleast 6 characters', 1000);
            return;
        }
        for (let i=0; i < userPassword.length; i++) {
            if (/[A-Z]/.test(userPassword[i])) {
                hasNoUpper = false;
            } else if (/[a-z]/.test(userPassword[i])) {
                hasNoLower = false;
            } else if (/\d/.test(userPassword[i])) {
                hasNoDigit = false;
            }
            if (hasNoLower === false && hasNoUpper === false && hasNoDigit === false) {
                break;
            }
        }
        if (hasNoUpper) {
            notifications.danger('Password should have atleast 1 uppercase character', 1000);
            return;
        }
        if (hasNoLower) {
            notifications.danger('Password should have atleast 1 lowercase character', 1000);
            return;
        }
        if (hasNoDigit) {
            notifications.danger('Password should have atleast 1 digit', 1000);
            return;
        }
        if(isNaN(parseFloat(userWage))) {
            notifications.danger('Invalid wage value', 1000);
            return;
        }
        if(parseFloat(userWage) < 0 ) {
            notifications.danger('Wage value cannot be less than zero', 1000);
            return;
        }
        if(userJoinDate === undefined) {
            notifications.danger('Join date is a required field', 1000);
            return;
        }
        if(chosenWageType === 0) {
            notifications.danger('Wage type is a required field', 1000);
            return;
        }
        if(chosenEmployeeType === 0) {
            notifications.danger('Employee type is a required field', 1000);
            return;
        }
        AddEmployee({
            firstName: userFirstName,
            lastName: userLastName,
            email: userEmail,
            password: userPassword,
            wage: parseFloat(userWage),
            joinDate: userJoinDate,
            employeeType: chosenEmployeeType,
            wageType: chosenWageType,
            project: projectId,
            contract: contract
        });
    }




</script>


<Toast />


<AdminComponent appName={appName} contentTitle={title} userFirstName={get(firstName)}>
    <div class="container">
        <form class="mt-7">
            {#if currentPage === "one"}
                <div class="flex flex-col gap-2 mt-1">
                    <label for="first_name">First Name</label>
                    <input name="first_name" class="rounded border-primary-800" type="text" id="first_name" bind:value={userFirstName}>
                </div>
                <div class="flex flex-col gap-2 mt-2">
                    <label for="last_name">Last Name</label>
                    <input name="last_name" class="rounded border-primary-800" type="text" id="last_name" bind:value={userLastName}>
                </div>
                <div class="flex flex-col gap-2 mt-2">
                    <label for="email">Email</label>
                    <input name="email" class="rounded border-primary-800" type="email" id="email" bind:value={userEmail}>
                </div>
                <div class="flex flex-col gap-2 mt-2">
                    <label for="password">Password</label>
                    <input name="password" class="rounded border-primary-800" type="password" id="password" bind:value={userPassword}>
                </div>
                <div class="flex flex-col gap-2 mt-2">
                    <label for="wage">Wage</label>
                    <input name="wage" class="rounded border-primary-800" type="text" id="wage" bind:value={userWageDisplay} on:input={updateUserWage}  >
                </div>
                <div class="flex flex-col gap-2 mt-3">
                    <Button height=12 width=36 label="Next" fontSize="sm" padding="8px" on:click={() => { currentPage = "two"}}   />
                </div>
            {:else}
            <div class="flex flex-col gap-2 mt-2">
                <label for="wage_type">Wage Type (click to add new type) <a href="/setup"><svg class="h-8 w-8 inline-block hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M17,12L12,17V14H8V10H12V7L17,12M21,16.5C21,16.88 20.79,17.21 20.47,17.38L12.57,21.82C12.41,21.94 12.21,22 12,22C11.79,22 11.59,21.94 11.43,21.82L3.53,17.38C3.21,17.21 3,16.88 3,16.5V7.5C3,7.12 3.21,6.79 3.53,6.62L11.43,2.18C11.59,2.06 11.79,2 12,2C12.21,2 12.41,2.06 12.57,2.18L20.47,6.62C20.79,6.79 21,7.12 21,7.5V16.5M12,4.15L5,8.09V15.91L12,19.85L19,15.91V8.09L12,4.15Z" /></svg></a></label>
                <select name="wage_type" class="rounded border-primary-800" id="wage_type" bind:value={chosenWageType}>
                    {#each wageTypes as type }
                        <option value={type.id}>{type.name}</option>
                    {/each}
                </select>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label for="employee_type">Employee Type (click to add new type) <a href="/setup"><svg class="h-8 w-8 inline-block hover:fill-primary-200" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M17,12L12,17V14H8V10H12V7L17,12M21,16.5C21,16.88 20.79,17.21 20.47,17.38L12.57,21.82C12.41,21.94 12.21,22 12,22C11.79,22 11.59,21.94 11.43,21.82L3.53,17.38C3.21,17.21 3,16.88 3,16.5V7.5C3,7.12 3.21,6.79 3.53,6.62L11.43,2.18C11.59,2.06 11.79,2 12,2C12.21,2 12.41,2.06 12.57,2.18L20.47,6.62C20.79,6.79 21,7.12 21,7.5V16.5M12,4.15L5,8.09V15.91L12,19.85L19,15.91V8.09L12,4.15Z" /></svg></a></label>
                <select name="employee_type" class="rounded border-primary-800" id="employee_type" bind:value={chosenEmployeeType}>
                    {#each employeeTypes as type }
                        <option value={type.id}>{type.name}</option>
                    {/each}
                </select>
            </div>
            <div class="flex flex-col gap-2 mt-2">
                <label for="join_date">Join Date</label>
                <input name="join_date" class="rounded border-primary-800" type="date" id="join_date" bind:value={userJoinDate}>
            </div>
            
            <div class="flex flex-col gap-2 mt-2">
                {#if contractChosen === false}
                    <label for="contract">Contract (Not required*)</label>
                    <input accept=".pdf" name="contract" class="rounded border-primary-800" type="file" id="contract" on:change={handleContractUpload}>
                {:else}
                    <div class="flex gap-4 items-center">
                        <p class="font-bold text-lg">File:</p>
                        <p class="italic text-base">{contract.name}</p>
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <!-- svelte-ignore a11y-no-static-element-interactions -->
                        <svg on:click={()=> removeContract()} class="h-7 w-7 hover:fill-primary-200 hover:cursor-pointer" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M21.12 22.54L19 20.41L16.88 22.54L15.47 21.12L17.59 19L15.47 16.88L16.88 15.47L19 17.59L21.12 15.47L22.54 16.88L20.41 19L22.54 21.12L21.12 22.54M14 2H6C4.89 2 4 2.89 4 4V20C4 21.11 4.89 22 6 22H13.81C13.28 21.09 13 20.05 13 19C13 15.69 15.69 13 19 13C19.34 13 19.67 13.03 20 13.08V8L14 2M13 9V3.5L18.5 9H13Z" /></svg>
                    </div>
                {/if}      
            </div>
            <div class="flex flex-col gap-2 mt-3">
                <Button height=12 width=36 label="Previous" fontSize="sm" padding="8px" on:click={() => { currentPage = "one"}}   />
            </div>
            
            <div class="mt-4 flex justify-center">
                {#if loading}
                    <Loader />
                {:else}
                    <Button 
                    height=12 width=36 label="Add Employee" fontSize="sm" padding="8px"
                    on:click={validateInput} />
                {/if}  
            </div>
            {/if}
            


            
        </form>
    </div>
</AdminComponent>