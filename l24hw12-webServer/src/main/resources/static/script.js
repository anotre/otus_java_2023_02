const newClientForm = document.getElementById("newClientForm");
newClientForm.addEventListener("submit", submitHandler);

function submitHandler(e) {
    e.preventDefault();
    const urlAction = "/api/clients";
    const request = buildPostRequest(urlAction);

    request.addEventListener("readystatechange", () => {
        if(request.readyState === 4 && request.status === 200) {
            window.location.reload();
        }
    });

    const serializedFormData = serializeForm(e.target)
    request.send(serializedFormData);

    function serializeForm(formNode) {
        const entries = Array.from(formNode.elements)
            .filter(field => !!field.name)
            .map((field, index) => {
                const { name, value } = field;
                return ((index === 0) ? "" : "&") + name + "=" + value;
            });

        return entries.join("");
    }

    function buildPostRequest(url) {
        const request = new XMLHttpRequest();
        request.open("POST", url, true);
        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        return request;
    }
}