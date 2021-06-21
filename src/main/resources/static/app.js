'use strict';

// Declare app level module which depends on views, and components
var myApp=angular.module('myApp', [
  'ngRoute',
  'myApp.view1',
  'myApp.view2',
  'myApp.version'
]);


myApp.controller('T2PController', function T2PController($scope) {

  $scope.displayInfo=true;
  $scope.loading=false;
  $scope.isBPMN = false;
  $scope.callback = function(pnml){
    $scope.displayInfo=false;
  $scope.pnml = pnml;
}

$scope.loadingCallback = function(loading){
  $scope.displayInfo=false;
$scope.loading = loading;
}
});
