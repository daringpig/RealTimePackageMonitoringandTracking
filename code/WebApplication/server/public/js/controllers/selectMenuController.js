angular.module('myModule')
	.controller('selectMenuCtrl',['selectService','$scope','$rootScope','$location','$routeParams','$timeout','$window',function(selectService,$scope,$rootScope,$location,$routeParams,$timeout, $window){

		$rootScope.typeOfService = 'dashboard';

		$scope.location=$location;

		$scope.selected = {};

		$rootScope.geocoder;

		$rootScope.geocoder = new google.maps.Geocoder();

		$scope.render = function(){
			
			if( ($rootScope.tid!=undefined || $rootScope.tid) && ($rootScope.pid!=undefined || $rootScope.pid) ){
					
				$scope.sideNavHrefs = {

					"dashboard" : "#/dashboard"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"map" : "#/map"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"temperature" : "#/temperature"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"humidity" : "#/humidity"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"vibration" : "#/vibration"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"shock" : "#/shock"+"/"+$rootScope.tid+"/"+$rootScope.pid
				};

			} else if($routeParams.truck_id && $routeParams.package_id) {
				console.log("in rps");
				$rootScope.tid = $routeParams.truck_id;
				$rootScope.pid = $routeParams.package_id;				

				$scope.sideNavHrefs = {

					"dashboard" : "#/dashboard"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"map" : "#/map"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"temperature" : "#/temperature"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"humidity" : "#/humidity"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"vibration" : "#/vibration"+"/"+$rootScope.tid+"/"+$rootScope.pid,
					"shock" : "#/shock"+"/"+$rootScope.tid+"/"+$rootScope.pid
				};


			} else {


					$scope.sideNavHrefs = {

						"dashboard" : "#/dashboard",
						"map" : "#/map",
						"temperature" : "#/temperature",
						"humidity" : "#/humidity",
						"vibration" : "#/vibration",
						"shock" : "#/shock"
					};
			}
		};		

		$scope.render();		

		$scope.setServiceType = function(service){

			$rootScope.typeOfService = service;
		}		
		

	  	$scope.urlFunc = function(){

	  		$rootScope.tid=$scope.selected.id.id;
	  		$rootScope.pid=$scope.selected.package;	  		

	    	$location.path($rootScope.typeOfService+'/'+$scope.selected.id.id+'/'+$scope.selected.package);
	    	
	    	$scope.render();
	    	

	    }; //end function urlFunc

	    $scope.packageUrl = function(){

	    	if($scope.txtpackage_id!='undefined'){

	    		$rootScope.pid=$scope.txtpackage_id;
	    		$scope.currentUrl=$location.path();
	    		$location.path($scope.currentUrl+'/'+$scope.txtpackage_id);
	    	}  	
	    }; //end function packageUrl	



		/*
			Code below if for updating trucks list and packages list
		*/


		
		$scope.trucks=[]; //holds the object for trucks list and packages list

		$scope.truckList =[];		

		var latestTimestamp='';		

		var packagesList = function(truck_id, isGrowl){

			var packages=[];					

			selectService.getPackages(truck_id)
			.then(function(data){				

				angular.forEach(data, function(value, key){

					packages.push(value);										
					
				});				
				
			});			

			return packages;
		}


		$scope.populatePackages = function(truck){

			var _packagesOf=[];

			if($scope.truckList.indexOf(truck)!=-1){

				var i=$scope.truckList.indexOf(truck);

				selectService.getPackages(truck)
				.then(function(packdata){

					angular.forEach(packdata, function(v, k){
						
						_packagesOf.push(v);
						
					});

				});

				$scope.trucks[i].packages=_packagesOf;
			}

		}		

		var truckPromise = selectService.getTrucks();

		truckPromise.then(function(data){

			//getting back a distinct array of trucks like [ ["1"],["2"],["3"] ] 
			//looping over array to form an array of trucks list

			if(data[0].length>0){			

				latestTimestamp=data[1];			

				angular.forEach(data[0], function(value, key){

					var newtruck=value.truck_id;

					var packs=[];

					$scope.truckList.push(newtruck);				

					var truckObj = {
						"id": newtruck,
						"packages": []
					};

					$scope.trucks.push(truckObj);
					
				});

			}	

			updater();			

		}); //end truckPromise.then

		var updater = function(action){			

			if(action==undefined){

          		var actionBy=0;

        	} else {

          		var actionBy=action;

        	}

			selectService.getLatest(latestTimestamp, actionBy)
			.then(function(data){	

				if(data[0].length>0){

					latestTimestamp=data[1];

					angular.forEach(data[0], function(value, key){

						var newtruck=value.truck_id;

						if($scope.truckList.indexOf(newtruck) < 0){

							//var newPacks = packagesList(newtruck);

							var newTruckObj = {

								"id": newtruck,
								"packages": []
							}

							$scope.truckList.push(newtruck); //update trucklist

							$scope.trucks.push(newTruckObj); //update truck object

							var mesg = "New truck added with Truck_id: "+newtruck;

							var options = {
								header: "New Truck Update",
								life: 8000
							};
							
							$.jGrowl(mesg, options);							

						} else {

							var options = {
								header: "New Package Update",
								life: 8000
							};

							var i=$scope.truckList.indexOf(newtruck);

							var newpacks=[];

							selectService.getPackages(newtruck)
							.then(function(packdata){

								angular.forEach(packdata, function(v, k){
									
									newpacks.push(v);
									
								});

							});

							$scope.trucks[i].packages=newpacks;

							$.jGrowl("New packages updated in Truck Id: " + newtruck, options);	
										
						}
						
					});
				
				}

			}); //end then

			
			$timeout(updater, 60000);


		} // end Updater	

		$scope.refresh = function(){

			updater(1);
		}

		$rootScope.openAddrs = function(lat,lng){

			var addrs = '';

			console.log(lat +" "+ lng);

			var latlng=new google.maps.LatLng(lat, lng); 

	        $rootScope.geocoder.geocode({'latLng': latlng}, function(results, status) {
	          
	          if (status == google.maps.GeocoderStatus.OK) {
	            if (results[1]) {

		            addrs=results[1].formatted_address;

	            	//$window.open("//maps.google.com/?q="+encodeURIComponent(addrs));
	            	$window.open("//maps.google.com/?q="+encodeURIComponent(addrs)+"@"+lat+lng);
	              
	            } else {

	            	console.log('No results found');
	            }
	          } else {
	            console.log('Geocoder failed due to: ' + status + " Click marker once again.");	            
	          }
	        }); 

		}

}]);
		
