'use strict';


customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">frontend documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                        <li class="link">
                            <a href="overview.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Overview
                            </a>
                        </li>
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-paper"></span>README
                            </a>
                        </li>
                        <li class="link">
                            <a href="dependencies.html" data-type="chapter-link">
                                <span class="icon ion-ios-list"></span>Dependencies
                            </a>
                        </li>
                    </ul>
                </li>
                    <li class="chapter modules">
                        <a data-type="chapter-link" href="modules.html">
                            <div class="menu-toggler linked" data-toggle="collapse" ${ isNormalMode ?
                                'data-target="#modules-links"' : 'data-target="#xs-modules-links"' }>
                                <span class="icon ion-ios-archive"></span>
                                <span class="link-name">Modules</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                        </a>
                        <ul class="links collapse" ${ isNormalMode ? 'id="modules-links"' : 'id="xs-modules-links"' }>
                            <li class="link">
                                <a href="modules/AppModule.html" data-type="entity-link">AppModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-AppModule-893331fe4651b428d3f633bbb939a7ba"' : 'data-target="#xs-components-links-module-AppModule-893331fe4651b428d3f633bbb939a7ba"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-AppModule-893331fe4651b428d3f633bbb939a7ba"' :
                                            'id="xs-components-links-module-AppModule-893331fe4651b428d3f633bbb939a7ba"' }>
                                            <li class="link">
                                                <a href="components/AccountManagerComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AccountManagerComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/AddStockComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AddStockComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/AppComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AppComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ChangeMinThresholdComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ChangeMinThresholdComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/CreateAccountComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">CreateAccountComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/DispatchChartComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">DispatchChartComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/DriverComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">DriverComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/LoginComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">LoginComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/MainMenuComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">MainMenuComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/OrderStockComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">OrderStockComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/POSComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">POSComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/QueryStockComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">QueryStockComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ReceiveStockComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ReceiveStockComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/RestaurantManagerComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">RestaurantManagerComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/RestaurantOrdersComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">RestaurantOrdersComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/RestaurantStockRemoveComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">RestaurantStockRemoveComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/TotalStockComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">TotalStockComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ViewOrderComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ViewOrderComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ViewPermissionsComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ViewPermissionsComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/ViewReceivedOrderComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">ViewReceivedOrderComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/WarehouseManagerComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">WarehouseManagerComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/WarehouseStockRemoveComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">WarehouseStockRemoveComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/AppRoutingModule.html" data-type="entity-link">AppRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/MaterialModule.html" data-type="entity-link">MaterialModule</a>
                            </li>
                </ul>
                </li>
                        <li class="chapter">
                            <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#injectables-links"' :
                                'data-target="#xs-injectables-links"' }>
                                <span class="icon ion-md-arrow-round-down"></span>
                                <span>Injectables</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                            <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links"' : 'id="xs-injectables-links"' }>
                                <li class="link">
                                    <a href="injectables/GMapService.html" data-type="entity-link">GMapService</a>
                                </li>
                                <li class="link">
                                    <a href="injectables/SessionService.html" data-type="entity-link">SessionService</a>
                                </li>
                            </ul>
                        </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#interfaces-links"' :
                            'data-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse" ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/ComparativeStockItem.html" data-type="entity-link">ComparativeStockItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/GraphData.html" data-type="entity-link">GraphData</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/IdToken.html" data-type="entity-link">IdToken</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MealOrder.html" data-type="entity-link">MealOrder</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/MealPrice.html" data-type="entity-link">MealPrice</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Message.html" data-type="entity-link">Message</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/OrderEntry.html" data-type="entity-link">OrderEntry</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/OrderId.html" data-type="entity-link">OrderId</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/StaffMember.html" data-type="entity-link">StaffMember</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/StockItem.html" data-type="entity-link">StockItem</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/StockName.html" data-type="entity-link">StockName</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/UserPermissions.html" data-type="entity-link">UserPermissions</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/VenueLocation.html" data-type="entity-link">VenueLocation</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/WarehouseStockRemoveObject.html" data-type="entity-link">WarehouseStockRemoveObject</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#miscellaneous-links"'
                            : 'data-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse" ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});