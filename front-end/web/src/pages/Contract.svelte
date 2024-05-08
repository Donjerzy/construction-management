<script>
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import {page} from '$app/stores';
    import { onMount } from 'svelte';
    const employeeId = $page.params.employeeId;
    let contract = "";
    let fetched = false;

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/employee/contract?employeeId=${employeeId}`, {
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
                contract = `data:application/pdf;base64,${result.contract}`;
                fetched = true;
            }
        })
    });

</script>

{#if fetched}
    <embed src={contract} type="application/pdf" style="width: 100vw; height: 100vh;">
{/if}
