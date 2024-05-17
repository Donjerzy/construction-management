<script>
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import { onMount } from 'svelte';
    let report = "";
    let fetched = false;

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/report/general`, {
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
                report = `data:application/pdf;base64,${result.report}`;
                fetched = true;
            }
        })
    });

</script>

{#if fetched}
    <embed src={report} type="application/pdf" style="width: 100vw; height: 100vh;">
{/if}
