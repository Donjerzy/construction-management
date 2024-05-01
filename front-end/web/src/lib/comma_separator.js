export default function commaSeparator(num) {
    let end;
    let result = [];
    if(num.includes(".")) {
        for(let i =0; i< num.length ;i++) {
            if(num[i]=== '.') {
                end = i;
                break;
            }
        }
    } else {
        end = num.length
    }

    result = Array.from(num.slice(end));

    let count = 0;

    for(let x = end - 1; x >= 0; x--) {
        if(count === 3) {
            result.unshift(',');
            count = 0;
            x+= 1;
        } else {
            result.unshift(num[x]);
            count += 1
        }   
    }

    return result.join("");
}