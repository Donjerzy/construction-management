<script>
    import EmployeeComponent from '../components/employee-component.svelte';
    import Button from '../components/button.svelte';
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';    
    import { get } from "svelte/store";
    import {onMount} from 'svelte';
    let contentTitle = 'My Expenses';

    let expenseSearchTerm = '';
    let expenses = [];

    $: filteredExpenses = expenses.filter((expense) => expense.title.toLowerCase().indexOf(expenseSearchTerm.toLowerCase()) !== -1);

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/expense/all`, {
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
                expenses = result.expenses;
            }
        });
    })
    
    function numberWithCommas(x) {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
</script>



<EmployeeComponent contentTitle={contentTitle}>
    <div class="mt-4 flex justify-end">
        <a href={`/employee/expense/add-expense`}><Button fontSize="base" height="10" label="Add Expense" padding="7" width="32" /> </a>
    </div>
    <div class="mt-4 pb-8 max-h-screen">
        <TableSearch placeholder="Search by title" hoverable={true} bind:inputValue={expenseSearchTerm} divClass="bg-slate-500 rounded">
            <Table divClass="max-h-80 overflow-auto" shadow>
                <TableHead defaultRow={false} theadClass="border-black ">
                    <tr class="bg-primary-100">
                        <TableHeadCell class="text-white">Title</TableHeadCell>
                        <TableHeadCell class="text-white">Expense Type</TableHeadCell>
                        <TableHeadCell class="text-white">Cost</TableHeadCell>
                        <TableHeadCell class="text-white">Date</TableHeadCell>
                        <TableHeadCell class="text-white">Document</TableHeadCell>
                        <TableHeadCell class="text-white">Note</TableHeadCell>
                    </tr>
                </TableHead>
                <TableBody>
                    {#each filteredExpenses as expense}
                        <TableBodyRow>
                            <TableBodyCell>{expense.title}</TableBodyCell>
                            <TableBodyCell>{expense.type}</TableBodyCell>
                            <TableBodyCell>{numberWithCommas(expense.cost)}</TableBodyCell>
                            <TableBodyCell>{expense.date}</TableBodyCell>
                            <TableBodyCell>
                                {#if expense.hasDocument === true}
                                    <div class="flex gap-4 items-center">
                                        <a target="_blank" class="underline hover:cursor-pointer hover:text-primary-200" href={`/employee/expense/document/${expense.expenseId}`}>View</a>
                                    </div>
                                {:else}
                                    <p>n/a</p>
                                {/if}     
                            </TableBodyCell>
                            <TableBodyCell>{expense.note}</TableBodyCell>
                        </TableBodyRow>
                    {/each}
                </TableBody>
            </Table> 
        </TableSearch>
    </div>

</EmployeeComponent>





