<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {page} from '$app/stores';
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    const projectId = $page.params.projectId;
    const taskId = $page.params.taskId;
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Assign Task"

</script>



<AdminComponent appName = {appName} contentTitle={contentTitle} userFirstName={get(firstName)}>

</AdminComponent>