<script>
    export let label;
    export let height;
    export let width;
    export let fontSize;
    export let padding;
</script>

<button on:click class="button" style="--height: {height}px; --width:{width}px; --f-size:{fontSize}; --padd:{padding}px">
    {label}
</button>


<style>
    button {
        background-color: var(--primary-clr);
        border: none;
        color: white;
        height: var(--height);
        width: var(--width);;
        padding: var(--padd);
        border-radius: 4px;
        font-size: var(--f-size);
    }
    button:hover {
        cursor: pointer;
        background-color: var(--tertiary-clr);
    }
</style>