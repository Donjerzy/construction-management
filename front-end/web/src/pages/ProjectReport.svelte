<script>
    import AdminComponent  from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import { Table, TableBody, TableBodyCell, TableBodyRow, TableHead, TableHeadCell, Checkbox, TableSearch } from 'flowbite-svelte';  
    import {onMount} from 'svelte';
    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction';
    let contentTitle = "Reports";

    let reports = [{ id: 1 ,name: "General Report"}]


    let searchTerm = '';
    
    $: filteredItems = reports.filter((report) => report.name.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1);


</script>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>

    <div>
        <div class="mt-4 pb-8">
            <TableSearch placeholder="Search by report name" hoverable={true} bind:inputValue={searchTerm}>
               <Table  shadow>
                    <TableHead defaultRow={false} theadClass="border-black">
                        <tr class="bg-primary-100">
                            <TableHeadCell class="text-white">#</TableHeadCell>
                            <TableHeadCell class="text-white">Name</TableHeadCell>
                            <TableHeadCell class="text-white">Action</TableHeadCell>
                        </tr>
                    </TableHead>
                    <TableBody>
                        {#each filteredItems as report}
                            <TableBodyRow>
                                <TableBodyCell>{report.id}</TableBodyCell>
                                <TableBodyCell>{report.name}</TableBodyCell>
                                <TableBodyCell class="flex gap-8">                
                                    <a target="_blank" class="underline hover:cursor-pointer hover:text-primary-200" href={`/report/general`}>View</a>
                            </TableBodyCell>
                            </TableBodyRow>
                        {/each}
                    </TableBody>
               </Table> 
            </TableSearch>
        </div>
    </div>


</AdminComponent>