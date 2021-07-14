'use strict';

// Declare app level module which depends on views, and components
var myApp=angular.module('myApp', [
  'ngRoute',
  'myApp.view1',
  'myApp.view2',
  'myApp.version'
]);


myApp.controller('T2PController', function T2PController($scope, downloadService, radioService) {

  $scope.displayInfo=true;
  $scope.loading=false;
  $scope.isBPMN = false;
  var newXmlStr="";
  $scope.callback = function(pnml){
    $scope.displayInfo=false;
  $scope.pnml = pnml;
}

$scope.loadingCallback = function(loading){
  $scope.displayInfo=false;
$scope.loading = loading;
}

$scope.isDownloadableMain = function (){
    if (downloadService.getIsDownloadable() === undefined || downloadService.getIsDownloadable() === false){
      return true;
    } else {
      return false;
    }
  }

  $scope.saveFile = function () {
    var s = new XMLSerializer();
    if(radioService.getIsPNML()){
      newXmlStr = downloadService.getContentPNML();
    }
    else if (radioService.getIsBPMN()){
      newXmlStr = downloadService.getContentBPMN();
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
});
