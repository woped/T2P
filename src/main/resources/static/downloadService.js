myApp.factory('downloadService', function() {
    var pnmlContent = "";
    var bpmnContent = "";
    var isDownloadable = false;

    var setContentPNML = function (pnmlContent){
        this.pnmlContent = pnmlContent;
    }
    var setContentBPMN = function (bpmnContent){
        this.bpmnContent = bpmnContent;
    }

    var getContentPNML = function(){
        return this.pnmlContent;
    };

    var getContentBPMN = function (){
        return this.bpmnContent;
    }

    var setDownloadableTrue = function (){
        this.isDownloadable = true;
    }

    var setDownloadableFalse = function (){
        this.isDownloadable = false;
    }

    var getIsDownloadable = function (){
        return this.isDownloadable;
    }

    return {
        setContentPNML: setContentPNML,
        setContentBPMN: setContentBPMN,
        getContentPNML: getContentPNML,
        getContentBPMN: getContentBPMN,
        setDownloadableTrue: setDownloadableTrue,
        setDownloadableFalse: setDownloadableFalse,
        getIsDownloadable: getIsDownloadable,
    };

});