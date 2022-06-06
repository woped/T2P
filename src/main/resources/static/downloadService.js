myApp.factory('downloadService', function() {
    var pnmlContent = "";
    var bpmnContent = "";
    var bpmn2Content = "";
    var isDownloadable = false;

    var setContentPNML = function (pnmlContent){
        this.pnmlContent = pnmlContent;
    }
    var setContentBPMN = function (bpmnContent){
        this.bpmnContent = bpmnContent;
    }
    var setContentBPMN2 = function (bpmn2Content){
        this.bpmn2Content = bpmn2Content;
    }

    var getContentPNML = function(){
        return this.pnmlContent;
    };

    var getContentBPMN = function (){
        return this.bpmnContent;
    }

    var getContentBPMN2 = function (){
        return this.bpmn2Content;
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
        setContentBPMN2: setContentBPMN2,
        getContentPNML: getContentPNML,
        getContentBPMN: getContentBPMN,
        getContentBPMN2: getContentBPMN2,
        setDownloadableTrue: setDownloadableTrue,
        setDownloadableFalse: setDownloadableFalse,
        getIsDownloadable: getIsDownloadable,
    };

});