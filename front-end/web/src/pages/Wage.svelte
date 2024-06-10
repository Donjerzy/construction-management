<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get, writable } from "svelte/store";
    import { Button } from 'flowbite-svelte';
    import { onMount } from "svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import Loader from "../components/loading-component.svelte";
    import {page} from '$app/stores';
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Wage";
    let loading = false;
    let employeeId = $page.params.employeeId;
    let projectId = $page.params.projectId;
    let currentPage = 'pay'; // pay || history
    let employeeWageType;
    let employeeWage;
    let paymentType = 'gen';
    let generatedAmount = 0;
    let noOfPeriod = 0;
    let nextValidPaymentDate;
    let customStartDate;
    let customEndDate;
    let customAmount;
    let customAmountDisplay;
    let wageNote = null;
    let wageHistory = [];
    let daysSinceLastPayment = 0;
    let automaticAmount = 0;
    let lastPaymentDate;


    // table history sort
    const sortKey = writable('periodStart'); // default sort key
    const sortDirection = writable(1); // default sort direction (ascending)
    const sortItems = writable(wageHistory.slice()); // make a copy of the items array

    // Define a function to sort the items
    const sortTable = (key) => {
        // If the same key is clicked, reverse the sort direction
        if ($sortKey === key) {
        sortDirection.update((val) => -val);
        } else {
        sortKey.set(key);
        sortDirection.set(1);
        }
    };

    $: {
        const key = $sortKey;
        const direction = $sortDirection;
        const sorted = [...$sortItems].sort((a, b) => {
        const aVal = a[key];
        const bVal = b[key];
        if (aVal < bVal) {
            return -direction;
        } else if (aVal > bVal) {
            return direction;
        }
        return 0;
        });
        sortItems.set(sorted);
    }

    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/wage-info?employee_id=${employeeId}`, {
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
                employeeWageType = result.wageInfo.type;
                employeeWage = parseFloat(result.wageInfo.wage);
                nextValidPaymentDate = result.wageInfo.nextValidPaymentDate;
            }
        })

        fetch(`http://localhost:8080/api/v1/employee/pay/generated?project_id=${projectId}&employee_id=${employeeId}`, {
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
                daysSinceLastPayment = result.generated.numberOfPeriod;
                automaticAmount = result.generated.amountToPay;
                lastPaymentDate = result.generated.lastPaymentDate;
            }
        })

        fetch(`http://localhost:8080/api/v1/employee/wage-history?employee_id=${employeeId}`, {
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
                wageHistory = result.wageHistory;
                sortItems.set(result.wageHistory);
            }
        })


    });


    function addOneMonthToCurrentDate(startDate) {
        const currentDate = new Date(startDate);
        // Get the current month and year
        let currentMonth = currentDate.getMonth();
        let currentYear = currentDate.getFullYear();

        // Add one month
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }

        // Update the date
        currentDate.setMonth(currentMonth);
        currentDate.setFullYear(currentYear);

        const formattedDate = currentDate.toLocaleDateString('en-CA', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        return formattedDate;
    }

    function addOneWeekToCurrentDate(startDate) {
        const currentDate = new Date(startDate);
        currentDate.setDate(currentDate.getDate() + 6);
        const formattedDate = currentDate.toLocaleDateString('en-CA', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        return formattedDate;
    }

    function addOneWeekToCurrentDateVariation(startDate) {
        const currentDate = new Date(startDate);
        currentDate.setDate(currentDate.getDate() + 7);
        const formattedDate = currentDate.toLocaleDateString('en-CA', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        return formattedDate;
    }

    function addOneDayToCurrentDate(startDate) {
        const currentDate = new Date(startDate);
        currentDate.setDate(currentDate.getDate() + 1);
        const formattedDate = currentDate.toLocaleDateString('en-CA', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        return formattedDate;
    }

    function formatWithCommas(value) {
        return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }


    function customAmountChange(event) {
        customAmount = event.target.value.replace(/,/g, '');
        customAmountDisplay = formatWithCommas(customAmount);
    }

    async function customPay() {
        let testStart = new Date(customStartDate);
        let testEnd = new Date(customEndDate);
        let testValid = new Date(nextValidPaymentDate);
        if(customStartDate === undefined) {
           return  notifications.danger("Start Date is a requered field", 1000);
        }
        if(testStart < testValid) {
            return notifications.danger("Invalid start date", 1000);
        }
        if(customEndDate === undefined) {
            return notifications.danger("End Date is a requered field", 1000);
        }
        if(testEnd < testStart) {
            return notifications.danger("Invalid date period", 1000);
        }
        if(customAmount <= 0 || customAmount === undefined || isNaN(parseFloat(customAmount))) {
            return notifications.danger("Invalid amount", 1000);
        }
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/employee/pay', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify({   
                employeeId: employeeId,
                amount: parseFloat(customAmount),
                startDate: customStartDate,
                endDate: customEndDate,
                note: wageNote
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
                notifications.success("Transaction recorded successfully", 1000);
                customAmount = 0;
                customAmountDisplay = "0";
                customStartDate = null;
                customEndDate = null;
                window.location.reload();
                return;
            }
        }).catch(error=> {
            loading = false;
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



    async function automaticPay() {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = String(currentDate.getMonth() + 1).padStart(2, '0');
        const day = String(currentDate.getDate()).padStart(2, '0');
        const formattedDate = `${year}-${month}-${day}`;
        loading = true;
        let error = false;
        let existsError = false;
        await fetch('http://localhost:8080/api/v1/employee/pay', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${get(accessToken)}`
            },
            body: JSON.stringify({   
                employeeId: employeeId,
                amount: parseFloat(automaticAmount),
                startDate: lastPaymentDate,
                endDate: formattedDate
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
                notifications.success("Transaction recorded successfully", 1000);
                noOfPeriod = 0;
                generatedAmount = 0;
                window.location.reload();
                return;
            }
        }).catch(error=> {
            loading = false;
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


    function calculateWage() {
        let amount = employeeWage * parseFloat(noOfPeriod);
        generatedAmount = amount;
    }


    function navigate(to) {
        currentPage = to;
    }



    



</script>

<svelte:head>
    <title>Wage</title>
</svelte:head>



<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <Toast />
    {#if currentPage === 'pay' }
        <div class="mt-10">
            <!-- Naviagtion-->
            <div class="flex justify-between h-8 align-middle text-base w-fit ml-auto mr-auto gap-20">
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link"  on:click={()=> navigate("pay")}>Pay</p>
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                    <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" on:click={()=> navigate("history")}>History</p>
            </div>
            <div class="mt-5">
                <div>
                    <label for="payment-type" class="block font-serif text-sm">Payment Type</label>
                    <select class="font-sans mt-2 w-[560px]"  id="payment-type" bind:value={paymentType}>
                        <option value="gen">Generated</option>
                        <option value="custom">Custom</option>
                    </select>
                </div>
                {#if paymentType === 'gen'}
                    <div class="mt-3">
                        <div class="flex gap-20 items-center w-full mt-5">
                            <div class="flex-col gap-40 w-full">
                                <p class="font-serif text-sm">Wage Type</p>
                                <p  class="italic border rounded w-full h-10 font-sans mt-1 border-primary-100 p-2 bg-primary-100">{employeeWageType}</p>
                            </div>
                            <div class="flex-col gap-40 w-full">
                                <label  for="number_of_periods" class="block font-serif text-sm">Days since last payment</label>
                                <p  class="italic border rounded w-full h-10 font-sans mt-1 border-primary-100 p-2 bg-primary-100">{daysSinceLastPayment}</p></div>
                            </div>

                        <div class="flex gap-20 items-center w-full mt-5">
                            <div class="flex-col gap-40 w-full">
                                <p class="font-serif text-sm">Employee wage</p>
                                <p  class="italic border mt-1 w-full rounded h-10 font-sans border-primary-100 p-2 bg-primary-100">{isNaN(employeeWage) ? 0 : numberWithCommas(employeeWage)}</p>
                            </div>
                            <div class="flex-col gap-40 w-full">
                                <p class="font-serif text-sm">Generated Amount</p>
                                <p  class="italic border mt-1 w-full rounded h-10 font-sans border-primary-100 p-2 bg-primary-100">{isNaN(automaticAmount) ? 0 : numberWithCommas(automaticAmount)}</p>
                            </div>
                        </div>
                        <div class="flex-col gap-40 mt-5">
                            {#if loading}
                                <Loader />
                            {:else}
                                <Button on:click={automaticPay} class="w-fit" color="dark">Pay Wage</Button>
                                {/if} 
                        </div>
                    </div>
                {:else}
                    <div class="mt-3">
                        <div class="flex gap-20 items-center w-full mt-5">
                            <div class="flex-col gap-40 w-full">
                                <label for="custom_start_date" class="block font-serif text-sm">Start Date</label>
                                <input class="font-sans mt-1 w-full h-10" type="date" id="custom_start_date" min={nextValidPaymentDate}  bind:value={customStartDate}>
                            </div>
                            <div class="flex-col gap-40 mt-2 w-full">
                                <label  for="custom_end_date" class="block font-serif text-sm">End Date</label>
                                <input class="font-sans mt-1 w-full h-10" type="date" id="custom_start_date" min={customStartDate}  bind:value={customEndDate}>
                            </div>
                        </div>
                        <div class="flex-col gap-40 mt-5">
                            <label for="custom_amount" class="block font-serif text-sm">Amount</label>
                            <input class="font-sans mt-1 w-[560px]" type="text" id="custom_amount" bind:value={customAmountDisplay} on:input={customAmountChange}>
                        </div>
                        <div class="mt-5 w-full">
                            <label class="block text-sm font-serif" for="note">Note</label>
                            <textarea class="font-sans w-full" bind:value={wageNote}  id="note"></textarea>
                        </div>
                    </div>
                    <div class="flex-col gap-40 mt-4">
                        {#if loading}
                            <Loader />
                        {:else}
                        <Button on:click={customPay} class="w-fit" color="dark">Pay Wage</Button>
                        {/if} 
                    </div>    
                {/if}

            </div> 
        </div>
    {:else if currentPage === 'history'}
    <div class="mt-10">
        <!-- Naviagtion-->
        <div class="flex justify-between h-8 align-middle text-base w-fit ml-auto mr-auto gap-20">
            <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200"  on:click={()=> navigate("pay")}>Pay</p>
                <!-- svelte-ignore a11y-click-events-have-key-events -->
                <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
                <p class="underline text-primary-900 hover:cursor-pointer hover:text-primary-200" id="active-link" on:click={()=> navigate("history")}>History</p>
        </div>
        <div class="mt-8">
            <Table divClass="max-h-80 overflow-auto" shadow hoverable={true}>
                <TableHead defaultRow={false} theadClass="border-black">
                    <tr class="bg-primary-100">
                        <TableHeadCell on:click={() => sortTable('periodStart')} class="text-white hover:cursor-pointer font-serif">Period Start</TableHeadCell>
                        <TableHeadCell on:click={() => sortTable('periodEnd')} class="text-white hover:cursor-pointer font-serif">Period End</TableHeadCell>
                        <TableHeadCell on:click={() => sortTable('amount')} class="text-white hover:cursor-pointer font-serif">Amount</TableHeadCell>
                        <TableHeadCell on:click={() => sortTable('transactionDate')} class="text-white hover:cursor-pointer font-serif">Transaction Date</TableHeadCell>
                        <TableHeadCell  class="text-white">Note</TableHeadCell>
                    </tr>
                </TableHead>
                <TableBody>
                    {#each $sortItems as history }
                        <TableBodyRow>
                            <TableBodyCell class="font-sans">{history.periodStart}</TableBodyCell>
                            <TableBodyCell class="font-sans">{history.periodEnd}</TableBodyCell>
                            <TableBodyCell class="font-sans">{numberWithCommas(history.amount)}</TableBodyCell>
                            <TableBodyCell class="font-sans">{history.transactionDate}</TableBodyCell>
                            <TableBodyCell class="font-sans">{history.note}</TableBodyCell>
                        </TableBodyRow>
                    {/each}
                </TableBody>
            </Table>
        </div>
    </div>
    {/if}
</AdminComponent>


<style>
    #active-link {
        color: var(--tertiary-clr);
        font-size: 1.2rem;
    }
</style>