import { GPSSiafeson } from 'gps-siafeson-plugin';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    GPSSiafeson.echo({ value: inputValue })
}
