<script>
    import {page} from '$app/stores';
    import IndividualEmployee from '../../../../pages/IndividualEmployee.svelte';
    const projectId = $page.params.projectId;
    const employeeId = $page.params.employeeId;
</script>



<IndividualEmployee employeeId={employeeId} projectId={projectId} />