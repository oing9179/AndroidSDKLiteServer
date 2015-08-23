<header>
    <!-- dropdown: "management" on navbar -->
    <ul id="dropdownManagementNavbar" class="dropdown-content">
        <li class="waves-effect waves-light"><a href="/dashboard/">Dashboard</a></li>
        <li class="waves-effect waves-light"><a href="/repository/xml/">XML Repository</a></li>
        <li class="waves-effect waves-light"><a href="/repository/zip/">ZIP Repository</a></li>
    </ul>
    <!-- navbar -->
    <nav>
        <div class="nav-wrapper indigo">
            <!-- brand logo for small screen -->
            <a href="" class="brand-logo waves-effect waves-light hide-on-small-and-down"
               style="padding-left: 12px; padding-right: 12px;">Android SDK Lite Server</a>
            <!-- brand logo for middle and large screen -->
            <a href="" class="brand-logo waves-effect waves-light hide-on-med-and-up"
               style="font-size: 20px; padding:0px 20px;">Android SDK Lite Server</a>
            <!-- toggle sidenav -->
            <a href="javascript:;" class="button-collapse" data-activates="sidenavLeftMain"><i class="material-icons">menu</i></a>
            <!-- navbar links -->
            <ul class="right hide-on-med-and-down">
                <li class="waves-effect waves-light">
                    <a href="/download_record/" class="waves-effect waves-light">Download record</a>
                </li>
                <li>
                    <a href="javascript:;" class="dropdown-button waves-effect waves-light"
                       data-activates="dropdownManagementNavbar">
                        Management <i class="material-icons right">arrow_drop_down</i>
                    </a>
                </li>
                <li>
                    <a href="/about" class="waves-effect waves-light">About</a>
                </li>
            </ul>
            <!-- sidenav links -->
            <ul id="sidenavLeftMain" class="side-nav">
                <li>
                    <a href="javascript:;">Download record</a>
                </li>
                <!-- horizontal divider -->
                <li style="padding: 0;">
                    <div class="divider"></div>
                </li>
                <!-- expanded "dropdown: management on navbar" -->
                <li>
                    <a href="/dashboard/">Dashboard</a>
                </li>
                <li>
                    <a href="/repository/xml/">XML Repository</a>
                </li>
                <li>
                    <a href="/repository/zip/">ZIP Repository</a>
                </li>
                <!-- horizontal divider -->
                <li style="padding: 0;">
                    <div class="divider"></div>
                </li>
                <li>
                    <a href="javascript:;">About</a>
                </li>
            </ul>
        </div>
    </nav>
    <div class="z-depth-1 indigo white-text" style="padding-left: 12px;">
        ${pageScope.navbarSubtitle}
    </div>
</header>