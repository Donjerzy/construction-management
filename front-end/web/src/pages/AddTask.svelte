<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { notifications } from "../lib/notification";
    import Toast from '../components/toast.svelte';
    import {page} from '$app/stores';
    


    const projectId = $page.params.projectId;
    let contentTitle = "Add Task"
    let appName = "Mjengo Bora Construction";
</script>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>

</AdminComponent>