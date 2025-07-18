import $ from 'jquery';

interface Team {
    title: string;
    description: string;
    flagImage: string;
}

interface TeamsResponse {
    teams: Team[];
    totalSize: number;
    currentPage: number;
    totalPages: number;
}

export default class Teams {

    private apiUrl: string;
    private $container: JQuery<HTMLElement>;
    private $pagination: JQuery<HTMLElement>;
    private page = 1;
    private size = 10;

    constructor() {
        console.log(this," Teams component initialized");
        const $root = $('[data-cmp-is="teams"]');
        this.apiUrl = $root.data('api');
        this.$container = $root.find('[data-cmp-hook-teams="teams-container"]');
        this.fetchTeams();
    }

    private fetchTeams() {
        $.getJSON(`${this.apiUrl}?page=${this.page}&size=${this.size}`, (data: TeamsResponse) => {
            this.renderTeams(data.teams);
        });
    }

    private renderTeams(teams: Team[]) {
        const $container = $(this.$container);
        $container.empty();
        teams.forEach((team, index) => {
            const flagImageUrl = `${team.flagImage}?random=${index}`;
            $container.append(`
                <div class="team-card">
                    <img src="${flagImageUrl}" alt="${team.title} flag" class="team-flag" />
                    <h3>${team.title}</h3>
                    <p>${team.description}</p>
                </div>
            `);
        });
    }
}