function copyTextarea(suffix) {
    const element = Array.from(document.querySelectorAll("textarea"))
        .find((textarea) => textarea.id.split(":")
            .some((part) => part === suffix || part === suffix + "_input"));
    if (!element) {
        return;
    }

    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(element.value);
        return;
    }

    element.focus();
    element.select();
    document.execCommand("copy");
}
