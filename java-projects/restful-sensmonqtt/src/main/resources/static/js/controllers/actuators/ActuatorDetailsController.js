/* global app */

app.controller('ActuatorDetailsController',
        ['$scope', '$timeout', '$routeParams', '$controller', 'ComponentService', 'actuatorDetails', 'values',
            function ($scope, $timeout, $routeParams, $controller, ComponentService, actuatorDetails, values) {
                var vm = this;

                // public
                function update() { // update deployment status
                    vm.deployer.processing = true;
                    ComponentService.isDeployed(vm.actuatorDetailsCtrl.item._links.deploy.href)
                            .then(
                                    function (deployed) {
                                        console.log('update: available, ' + deployed);
                                        vm.deployer.processing = false;
                                        vm.deployer.available = true;
                                        vm.deployer.deployed = deployed;
                                    },
                                    function (response) {
                                        console.log('update: unavailable');
                                        vm.deployer.processing = false;
                                        vm.deployer.available = false;
                                    });
                }
                
                $scope.isCollapsedLog = false;

                function deploy() {
                    vm.deployer.processing = true;
                    ComponentService.deploy(vm.deployer.deploy, vm.actuatorDetailsCtrl.item._links.deploy.href)
                            .then(
                                    function (response) {
                                        vm.deployer.processing = false;
                                        vm.deployer.deploy.deployed = true;
                                        vm.deployer.deploy.success = 'Deployed successfully';
                                        vm.deployer.update();
                                    },
                                    function (response) {
                                        vm.deployer.processing = false;
                                        vm.deployer.deploy.errors = response;
                                        vm.deployer.deploy.errors.global = 'Error on deployment, please try again';
                                        vm.deployer.update();
                                    });
                }

                function undeploy() {
                    vm.deployer.processing = true;
                    vm.deployer.deploy.success = undefined;
                    vm.deployer.deploy.error = undefined;
                    ComponentService.undeploy(vm.actuatorDetailsCtrl.item._links.deploy.href)
                            .then(
                                    function (response) {
                                        vm.deployer.processing = false;
                                        vm.deployer.deployed = false;
                                        vm.deployer.deploy.success = 'Undeployed successfully';
                                        vm.deployer.update();
                                    },
                                    function (response) {
                                        vm.deployer.processing = false;
                                        vm.deployer.deploy.errors = response;
                                        vm.deployer.deploy.errors.global = 'Error on undeployment, please try again';
                                        vm.deployer.update();
                                    });
                }

                angular.extend(vm, {
                    deployer: {
                        deploy: {
                            component: ComponentService.COMPONENT.ACTUATOR
                        },
                        update: update,
                        doDeploy: deploy,
                        doUndeploy: undeploy
                    },
                    values: values
                });

                // expose controller ($controller will auto-add to $scope)
                angular.extend(vm, {
                    actuatorDetailsCtrl: $controller('ItemDetailsController as actuatorDetailsCtrl',
                            {
                                $scope: $scope,
                                item: actuatorDetails
                            })
                });
                
                // VERY IMPORTANT LINE HERE
                update();
            }]);