myApp.factory('radioService', function() {
    var isBPMN = false;
    var isPNML = false;

    var setBPMN = function (){
        isBPMN = true;
        isPNML = false;
    }

    var setPNML = function (){
        isPNML = true;
        isBPMN = false;
    }

    var getIsBPMN = function(){
        return isBPMN;
    };
    var getIsPNML = function(){
        return isPNML;
    };

    return {
        setBPMN: setBPMN,
        setPNML: setPNML,
        getIsBPMN: getIsBPMN,
        getIsPNML: getIsPNML
    };

});