function copyTextarea(suffix) {
    const element = Array.from(document.querySelectorAll("textarea"))
        .find((textarea) => textarea.id.endsWith(":" + suffix));
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
