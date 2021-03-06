<header>
    <!-- dropdown: "management" on navbar -->
    <ul id="dropdownManagementNavbar" class="dropdown-content">
        <li class="waves-effect"><a href="admin/dashboard/">Dashboard</a></li>
        <li class="waves-effect"><a href="admin/repository/xml/">XML Repository</a></li>
        <li class="waves-effect"><a href="admin/repository/zip/">ZIP Repository</a></li>
    </ul>
    <!-- navbar -->
    <nav>
        <div class="nav-wrapper indigo">
            <!-- brand logo for small screen -->
            <a href="" class="brand-logo waves-effect waves-light hide-on-small-and-down"
               style="padding:0 12px;">Android SDK Lite Server</a>
            <!-- brand logo for middle and large screen -->
            <a href="" class="brand-logo waves-effect waves-light hide-on-med-and-up"
               style="font-size: 20px; padding:0 10px;">Android SDK Lite Server</a>
            <!-- toggle sidenav -->
            <a href="javascript:" class="button-collapse" data-activates="sidenavLeftMain">
                <i class="material-icons">menu</i>
            </a>
            <!-- navbar links -->
            <ul class="right hide-on-med-and-down">
                <li class="waves-effect">
                    <a href="" class="waves-effect waves-light">Home</a>
                </li>
                <li>
                    <a href="javascript:;" class="dropdown-button waves-effect waves-light"
                       data-activates="dropdownManagementNavbar">
                        Management <i class="material-icons right">arrow_drop_down</i>
                    </a>
                </li>
            </ul>
            <!-- sidenav links -->
            <ul id="sidenavLeftMain" class="side-nav">
                <li>
                    <a href="">Home page</a>
                </li>
                <!-- horizontal divider -->
                <li style="padding: 0;">
                    <div class="divider"></div>
                </li>
                <!-- expanded: dropdown: management on navbar" -->
                <li>
                    <a href="admin/dashboard/">Dashboard</a>
                </li>
                <li>
                    <a href="admin/repository/xml/">XML Repository</a>
                </li>
                <li>
                    <a href="admin/repository/zip/">ZIP Repository</a>
                </li>
            </ul>
        </div>
    </nav>
</header>