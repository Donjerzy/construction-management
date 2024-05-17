<script>
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import { onMount } from 'svelte';
    import {page} from '$app/stores';
    let report = "";
    let fetched = false;
    const projectId = $page.params.projectId;

    onMount(()=> {
        let errorFetch = false;
        fetch(`http://localhost:8080/api/v1/report/project-general?project=${projectId}`, {
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
