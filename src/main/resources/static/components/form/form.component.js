'use strict';
var rawResponse = function (value) {
    return value;
};
angular.module('myApp').component('t2pForm', {
    templateUrl: '/t2p/components/form/form.html',
    bindings: {
        pnml: '<',
        loading: '<',
        isBPMN: '<'
    },
    controller: function T2pFormController($http, $scope, radioService, downloadService) {
        var helper = "";
        var helperTwo ="";
        $scope.pnml = this.pnml;
        $scope.loading = this.loading;
        $scope.isBPMN = this.isBPMN;
        $scope.previousText = null;
        var newXmlStr = "";
        $scope.isGeneratable = function () {
            if ($scope.text == null) {
                return true;
            } else {
                return !($scope.text.length > 0);
            }
        }

        $scope.isDownloadable = function (){
            if (helperTwo === "" && helper === ""){
                return true;
            } else {
                return false;
            }
        }

        $scope.clearText = function () {
            $scope.text = "";
        }
        $scope.isClearable = function () {
            if ($scope.text == null) {
                return true;
            } else {
                return !($scope.text.length > 0);
            }
        }

        $scope.saveFile = function () {
                var s = new XMLSerializer();
                if(radioService.getIsPNML()){
                    newXmlStr = s.serializeToString(helper);

                } else if (radioService.getIsBPMN()) {
                    newXmlStr = s.serializeToString(helperTwo);
                    //var newXmlStr = "test"
                }
                var blob = new Blob([newXmlStr], { type:"application/json;charset=utf-8;" });
                var downloadLink = angular.element('<a></a>');
                downloadLink.attr('href',window.URL.createObjectURL(blob));
                if (radioService.getIsPNML()){	
					downloadLink.attr('download', 'processmodel.pnml');
				} else if (radioService.getIsBPMN()){
					downloadLink.attr('download', 'processmodel.bpmn');
				}
                downloadLink[0].click();
        };


        $scope.generate = function () {
            var s = new XMLSerializer();

            if (!($scope.text === $scope.previousText)) {
                $scope.loading(true);
                var req = {
                    method: 'POST',
                    url: '/t2p/generatePNML',
                    transformResponse: rawResponse,
                    headers: {
                        'Content-Type': "application/json"
                    },
                    data: $scope.text
                }

                $http(req).then(function (response) {
                    var parser = new DOMParser();
                    var xmlDoc = parser.parseFromString(response.data, "text/xml");
                    helper = parser.parseFromString(response.data, "text/xml");
                    downloadService.setContentPNML(s.serializeToString(helper));
                    downloadService.setDownloadableTrue();
                    $scope.loading(false);
                    $scope.pnml(xmlDoc);
                    $scope.previousText = $scope.text;
                }, function (response) {
                    $scope.loading(false);
                    var message = "An unexspected Error Occured";

                    if (response.status === 400) {
                        message = "You used unallowed characters eg.<=#"
                    }
                    if (response.status === 500) {
                        message = "We were not able to create a Petrinet based on your text. Make sure you stick to natural languages grammar and syntax."
                    }
                    if (response.status === 503) {
                        message = "Our servers are currently busy. Try again in a few minutes."
                    }

                    swal({
                        title: "Error!",
                        text: message,
                        icon: "error",
                        button: "Ok then...",
                    });
                });
            }
            $scope.generateBPMN();
        }

        $scope.generateBPMN = function () {
            var s = new XMLSerializer();
            var req = {
                    method: 'POST',
                    url: '/t2p/generateBPMN',
                    transformResponse: rawResponse,
                    headers: {
                        'Content-Type': "application/json"
                    },
                    data: $scope.text
                }

                $http(req).then(function (response) {
                    var parser = new DOMParser();
                    helperTwo = parser.parseFromString(response.data, "text/xml");
                    downloadService.setContentBPMN(s.serializeToString(helperTwo));
                    downloadService.setDownloadableTrue();
                });
        }
        $scope.getValue = function (){
            return this.isBPMN;
        }
    }
});
