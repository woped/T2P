myApp.factory('radioService', function() {
    var isBPMN = false;
    var isPNML = false;
    var isBPMN2 = false

    var setBPMN = function (){
        isBPMN2 = false;
        isBPMN = true;
        isPNML = false;
    }

    var setBPMN2 = function (){
        isBPMN2 = true;
        isBPMN = false;
        isPNML = false;
    }

    var setPNML = function (){
        isBPMN2 = false;
        isPNML = true;
        isBPMN = false;
    }

    var getIsBPMN = function(){
        return isBPMN;
    };
    var getIsPNML = function(){
        return isPNML;
    };
    var getIsBPMN2 = function(){
        return isBPMN2;
    }

    return {
        setBPMN2: setBPMN2,
        setBPMN: setBPMN,
        setPNML: setPNML,
        getIsBPMN2: getIsBPMN2,
        getIsBPMN: getIsBPMN,
        getIsPNML: getIsPNML
    };

});