<script>
    import {firstName} from '../stores.js' 
    import { get } from "svelte/store";
    import AdminComponent from '../components/admin-component.svelte';
    let userFirstName = get(firstName);
    let appName = 'Mjengo Bora Construction'
</script>


<svelte:head>
    <title>Dashboard</title>
</svelte:head>


<AdminComponent appName={appName} userFirstName={userFirstName} contentTitle="Dashboard">
</AdminComponent>


<style></style>