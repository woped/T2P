'use strict';

//defines the colors for Splits
var xorSplitColors = ['lightblue', 'red', 'blue', 'orange', 'yellow'];
var andSplitColors = ['lime','cyan','lightgreen','purple','pink'];
var xorColor=0;
var andColor=0;

    //chooses matching colors for the splits labels in the input text
    function wrapWords(str, node) {
      //substring eliminates "choice if " from the label
      if (node.label.includes("choice if")){
        node.label=node.label.substring(10);
      }
      //chooses the color depending on the splits type
      if (node.gatewayType=='104'){
        return str.replace(node.label, ($0) => {
            return `<span style="background:${xorSplitColors[xorColor]}; display:inline-block; padding:0.2rem 0.5rem; margin: 0 0.2rem">${$0}</span>`;
        });
        xorColor++;
      } else if (node.gatewayType=='101'){
            return str.replace(node.label, ($0) => {
                return `<span style="background:${andSplitColors[andColor]}; display:inline-block; padding:0.2rem 0.5rem; margin: 0 0.2rem">${$0}</span>`;
            });
            andColor++;
        }
    }
    //colorizes the input-text
    function colorizeText(node, gatewayID) {
      const element = document.querySelector('.text-output');
      console.log("Split Information: ", node);
      if (element) {
        element.innerHTML = wrapWords(element.dataset.text, node);
      }
    }

var test = true;

angular.module('myApp').
  component('petrinet', {
    template: '<div id="mynetwork" style="height:100%;width:100%; margin-bottom: 50%"></div>',
    bindings: {
   pnml: '<'
      },
    controller: function petrinetController(radioService) {
        radioService.setPNML();

      var generateWorkFlowNet=true;//Determines wether WoPeD specific Elements like XOR Split are created

      function generatePetrinet(petrinet){
        var data=getVisElements(petrinet);

        // create a network
        var container = document.getElementById('mynetwork');

        var options = {
          layout: {
          randomSeed: undefined,
          improvedLayout:true,
          hierarchical: {
          enabled:true,
          levelSeparation: 150,
          nodeSpacing: 100,
          treeSpacing: 200,
          blockShifting: true,
          edgeMinimization: true,
          parentCentralization: true,
          direction: 'LR',        // UD, DU, LR, RL
          sortMethod: 'directed'   // hubsize, directed
          }
      },
          groups: {
            places: {color:{background:'#4DB6AC',border: '#00695C'}, borderWidth:3, shape: 'circle'},
            transitions: {color:{background:'#FFB74D',border: '#FB8C00',}, shape: 'square', borderWidth:3},
            andJoin: {color:{background:'#DCE775',border: '#9E9D24',}, shape: 'square', borderWidth:3},
            //andSplit: {color:{background:'#DCE775',border: '#9E9D24',}, shape: 'square', borderWidth:3},
            andSplit1: {color:{background:andSplitColors[0],border: '#9E9D24',}, shape: 'square', borderWidth:3},
            andSplit2: {color:{background:andSplitColors[1],border: '#9E9D24',}, shape: 'square', borderWidth:3},
            andSplit3: {color:{background:andSplitColors[2],border: '#9E9D24',}, shape: 'square', borderWidth:3},
            andSplit4: {color:{background:andSplitColors[3],border: '#9E9D24',}, shape: 'square', borderWidth:3},
            andSplit5: {color:{background:andSplitColors[4],border: '#9E9D24',}, shape: 'square', borderWidth:3},
            //xorSplit: {color:{background:'#9575CD', border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorSplit1: {color:{background:xorSplitColors[0], border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorSplit2: {color:{background:xorSplitColors[1], border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorSplit3: {color:{background:xorSplitColors[2], border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorSplit4: {color:{background:xorSplitColors[3], border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorSplit5: {color:{background:xorSplitColors[4], border: '#512DA8',}, shape: 'square', borderWidth:3,image:"/img/and_split.svg"},
            xorJoin: {color:{background:'#9575CD',border: '#512DA8',}, shape: 'square', borderWidth:3}
            },
      interaction:{
        zoomView:true,
        dragView:true
      }
    }
        // initialize your network!
        var network = new vis.Network(container, data, options);
      }


      this.$onInit = function () {
        if(!this.pnml===undefined){
        generatePetrinet(getPetriNet(this.pnml));
      }
          };

          this.$onChanges = function (changes) {
            if(typeof changes.pnml.currentValue !== 'undefined'){
              generatePetrinet(getPetriNet(changes.pnml.currentValue));
            }
        }

        var gateways=[];
           function getPetriNet( PNML ){
             var places=PNML.getElementsByTagName("place")
             var transitions=PNML.getElementsByTagName("transition")
             var arcs=PNML.getElementsByTagName("arc")
             var petrinet = {
               places:[],
               transitions:[],
               arcs:[]
             }

             for (var x=0;x<arcs.length;x++){
                 var arc = arcs[x];
                 petrinet.arcs.push({id:arc.getAttribute("id"),source:arc.getAttribute("source"),target:arc.getAttribute("target")})
             }

             for (var x=0;x<places.length;x++){
                 var place = places[x];
                 petrinet.places.push({id:place.getAttribute("id"),label:place.getElementsByTagName("text")[0].textContent})
             }

             for (var x=0;x<transitions.length;x++){
                 var transition = transitions[x];
                 var isGateway = transition.getElementsByTagName("operator").length >0;
                 var gatewayType=undefined;
                 var gatewayID=undefined;
                 if(isGateway){
                   gatewayType=transition.getElementsByTagName("operator")[0].getAttribute("type");
                   gatewayID=transition.getElementsByTagName("operator")[0].getAttribute("id");
                 }
                 petrinet.transitions.push({id:transition.getAttribute("id"),label:transition.getElementsByTagName("text")[0].textContent,isGateway:isGateway,gatewayType:gatewayType,gatewayID:gatewayID})
             }

             return petrinet;
           }

           function resetGatewayLog(){
             gateways=[];
           }

           function logContainsGateway(transition){
             for (var x=0;x<gateways.length;x++){
               if(gateways[x].gatewayID===transition.gatewayID)
               return true;
             }
             return false;
           }

           function logGatewayTransition(transition){
                if(logContainsGateway(transition)===true){
                  for (var x=0;x<gateways.length;x++){
                    if(gateways[x].gatewayID===transition.gatewayID)
                    gateways[x].transitionIDs.push({transitionID:transition.id});
                  }
                }else{
                gateways.push({gatewayID:transition.gatewayID, transitionIDs:[{transitionID:transition.id}]});
                }
           }

           function getGatewayIDsforReplacement(arc){
             var replacement ={source:null,target:null};
             for (var x=0;x<gateways.length;x++){
               for (var i=0;i<gateways[x].transitionIDs.length;i++){
                  if(arc.source===gateways[x].transitionIDs[i].transitionID){
                      replacement.source=gateways[x].gatewayID;
                  }
                  if(arc.target===gateways[x].transitionIDs[i].transitionID){
                      replacement.target=gateways[x].gatewayID;
                  }
               }


             }
            return replacement;
           }

           function replaceGatewayArcs(arcs){

             for (var x=0;x<arcs.length;x++){
                var replacement=getGatewayIDsforReplacement(arcs[x]);
                  if(replacement.source!==null){
                    arcs[x].source=replacement.source;
                  }
                  if(replacement.target!==null){
                    arcs[x].target=replacement.target;
                  }

             }
           }


           function getVisElements(PetriNet){
             // provide the data in the vis format
             var edges = new vis.DataSet([]);
             var nodes = new vis.DataSet([ ]);
             for (var x=0;x<PetriNet.places.length;x++){
               nodes.add({id: PetriNet.places[x].id,group:"places", label: PetriNet.places[x].label});
             }

            var counter=1
             for (var x=0;x<PetriNet.transitions.length;x++){
             //console.log("xorSplit transitions: ", PetriNet.transitions[x])
               if(!PetriNet.transitions[x].isGateway || generateWorkFlowNet===false){
                 nodes.add({id: PetriNet.transitions[x].id,group:"transitions", label: PetriNet.transitions[x].id,title:PetriNet.transitions[x].label});
               }
               else{
                  var gatewayGroup="";
                  var label="";
                 switch(PetriNet.transitions[x].gatewayType) {
                   case "101":
                    gatewayGroup="andSplit";
                    colorizeText(PetriNet.transitions[x], x)
                    break;
                   case "102":
                    gatewayGroup="andJoin";
                    break;
                   case "104":
                    gatewayGroup="xorSplit"+String(counter);
                    counter++;
                    colorizeText(PetriNet.transitions[x], x)
                    break;
                   case "105":
                    gatewayGroup="xorJoin";
                    break;

                  }
                  if(!logContainsGateway(PetriNet.transitions[x])){
                    nodes.add({id: PetriNet.transitions[x].gatewayID,group:gatewayGroup, label:label ,title:PetriNet.transitions[x].label});

                  }
                  logGatewayTransition(PetriNet.transitions[x]);
               }

             }

             if(generateWorkFlowNet===true){
               replaceGatewayArcs(PetriNet.arcs);
             }


             for (var x=0;x<PetriNet.arcs.length;x++){
               edges.add({from: PetriNet.arcs[x].source, to: PetriNet.arcs[x].target, arrows:"to"})
             }
             resetGatewayLog();
             return {nodes:nodes,edges:edges};

           }
         }


  });

angular.module('myApp').
component('bpmn', {
    template: '<div id="mynetwork" style="height:100%;width:100%; margin-bottom: 50%"></div>',
    bindings: {
        pnml: '<'
    },
    controller: function petrinetController(radioService) {

        radioService.setBPMN();

        var generateWorkFlowNet=true;//Determines wether WoPeD specific Elements like XOR Split are created

        function generatePetrinet(petrinet){
            var data=getVisElements(petrinet);

            // create a network
            var container = document.getElementById('mynetwork');

            var options = {
                layout: {
                    randomSeed: undefined,
                    improvedLayout:true,
                    hierarchical: {
                        enabled:true,
                        levelSeparation: 150,
                        nodeSpacing: 100,
                        treeSpacing: 200,
                        blockShifting: true,
                        edgeMinimization: true,
                        parentCentralization: true,
                        direction: 'LR',        // UD, DU, LR, RL
                        sortMethod: 'directed'   // hubsize, directed
                    }
                },
                groups: {
                    places: {color:{background:'#FFFFFF',border: '#000000'}, borderWidth:2, shape: 'circle'},
                    transitions: {color:{background:'#FFFFFF',border: '#000000',}, shape: 'star', borderWidth:2},
                    andJoin: {color:{background:'#FFFFFF',border: '#000000',}, shape: 'triangle', borderWidth:3},
                    andSplit: {color:{background:'#FFFFFF',border: '#000000',}, shape: 'triangle', borderWidth:3},
                    xorSplit: {color:{background:'#FFFFFF',border: '#000000',}, shape: 'triangleDown', borderWidth:3,image:"/img/and_split.svg"},
                    xorJoin: {color:{background:'#FFFFFF',border: '#000000',}, shape: 'triangleDown', borderWidth:3}
                },
                interaction:{
                    zoomView:true,
                    dragView:true
                }
            }
            // initialize your network!
            var network = new vis.Network(container, data, options);
        }


        this.$onInit = function () {
            if(!this.pnml===undefined){
                generatePetrinet(getPetriNet(this.pnml));
            }
        };

        this.$onChanges = function (changes) {
            if(typeof changes.pnml.currentValue !== 'undefined'){
                generatePetrinet(getPetriNet(changes.pnml.currentValue));
            }
        }

        var gateways=[];
        function getPetriNet( PNML ){
            var places=PNML.getElementsByTagName("place")
            var transitions=PNML.getElementsByTagName("transition")
            var arcs=PNML.getElementsByTagName("arc")
            var petrinet = {
                places:[],
                transitions:[],
                arcs:[]
            }

            for (var x=0;x<arcs.length;x++){
                var arc = arcs[x];
                petrinet.arcs.push({id:arc.getAttribute("id"),source:arc.getAttribute("source"),target:arc.getAttribute("target")})
            }

            for (var x=0;x<places.length;x++){
                var place = places[x];
                petrinet.places.push({id:place.getAttribute("id"),label:""})
            }

            for (var x=0;x<transitions.length;x++){
                var transition = transitions[x];
                var isGateway = transition.getElementsByTagName("operator").length >0;
                var gatewayType=undefined;
                var gatewayID=undefined;
                if(isGateway){
                    gatewayType=transition.getElementsByTagName("operator")[0].getAttribute("type");
                    gatewayID=transition.getElementsByTagName("operator")[0].getAttribute("id");
                }
                petrinet.transitions.push({id:transition.getAttribute("id"),label:transition.getElementsByTagName("text")[0].textContent,isGateway:isGateway,gatewayType:gatewayType,gatewayID:gatewayID})
            }

            return petrinet;
        }

        function resetGatewayLog(){
            gateways=[];
        }

        function logContainsGateway(transition){
            for (var x=0;x<gateways.length;x++){
                if(gateways[x].gatewayID===transition.gatewayID)
                    return true;
            }
            return false;
        }

        function logGatewayTransition(transition){
            if(logContainsGateway(transition)===true){
                for (var x=0;x<gateways.length;x++){
                    if(gateways[x].gatewayID===transition.gatewayID)
                        gateways[x].transitionIDs.push({transitionID:transition.id});
                }
            }else{
                gateways.push({gatewayID:transition.gatewayID, transitionIDs:[{transitionID:transition.id}]});
            }
        }

        function getGatewayIDsforReplacement(arc){
            var replacement ={source:null,target:null};
            for (var x=0;x<gateways.length;x++){
                for (var i=0;i<gateways[x].transitionIDs.length;i++){
                    if(arc.source===gateways[x].transitionIDs[i].transitionID){
                        replacement.source=gateways[x].gatewayID;
                    }
                    if(arc.target===gateways[x].transitionIDs[i].transitionID){
                        replacement.target=gateways[x].gatewayID;
                    }
                }


            }
            return replacement;
        }

        function replaceGatewayArcs(arcs){

            for (var x=0;x<arcs.length;x++){
                var replacement=getGatewayIDsforReplacement(arcs[x]);
                if(replacement.source!==null){
                    arcs[x].source=replacement.source;
                }
                if(replacement.target!==null){
                    arcs[x].target=replacement.target;
                }

            }
        }


        function getVisElements(PetriNet){
            // provide the data in the vis format
            var edges = new vis.DataSet([]);
            var nodes = new vis.DataSet([ ]);
            for (var x=0;x<PetriNet.places.length;x++){
                nodes.add({id: PetriNet.places[x].id,group:"places", label: PetriNet.places[x].label});
            }

            for (var x=0;x<PetriNet.transitions.length;x++){
                if(!PetriNet.transitions[x].isGateway || generateWorkFlowNet===false){
                    nodes.add({id: PetriNet.transitions[x].id,group:"transitions", label: PetriNet.transitions[x].label,title:PetriNet.transitions[x].label});
                }
                else{
                    var gatewayGroup="";
                    var label="";
                    switch(PetriNet.transitions[x].gatewayType) {
                        case "101":
                            gatewayGroup="andSplit";
                            break;
                        case "102":
                            gatewayGroup="andJoin";
                            break;
                        case "104":
                            gatewayGroup="xorSplit";
                            break;
                        case "105":
                            gatewayGroup="xorJoin";
                            break;

                    }
                    if(!logContainsGateway(PetriNet.transitions[x])){
                        nodes.add({id: PetriNet.transitions[x].gatewayID,group:gatewayGroup, label:label ,title:PetriNet.transitions[x].label});

                    }
                    logGatewayTransition(PetriNet.transitions[x]);
                }

            }

            if(generateWorkFlowNet===true){
                replaceGatewayArcs(PetriNet.arcs);
            }


            for (var x=0;x<PetriNet.arcs.length;x++){
                edges.add({from: PetriNet.arcs[x].source, to: PetriNet.arcs[x].target, arrows:"to"})
            }
            resetGatewayLog();
            return {nodes:nodes,edges:edges};

        }
    }


});
