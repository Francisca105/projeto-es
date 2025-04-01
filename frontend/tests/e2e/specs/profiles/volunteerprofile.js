describe('VolunteerProfile', () => {
    beforeEach(() => {
      cy.deleteAllButArs();
      cy.createDemoEntities();
      cy.demoVolunteerLogin();
    });
  
    afterEach(() => {
      cy.logout();
      cy.deleteAllButArs();
    });
  
    it('successfully creates volunteer profile without activities', () => {
      const SHORT_BIO = 'Test short bio for Cypress';
  
      // Intercept profile API calls
      cy.intercept('GET', '/profiles/volunteer/2').as('getProfile');
      cy.intercept('POST', '/profiles/volunteer/2').as('createProfile');
  
      // Navigate to profile page
      cy.visit('/profiles/volunteer/2');
  
      // Verify initial state - no profile
      cy.get('[data-cy=userLoginButton]')
        .should('exist')
        .and('contain', 'CREATE MY PROFILE');
  
      // Open creation dialog
      cy.get('[data-cy=userLoginButton]').click();

      // Write short bio
      cy.get('[data-cy="volunteerProfileCreationShortBio"]').type(SHORT_BIO);
  
      // Save profile
      cy.get('.v-dialog').contains('SAVE').click();
  
      // Wait for API response
      cy.wait('@getProfile').its('response.statusCode').should('eq', 200);
  
      // Verify profile details
      cy.get('.text-description p')
        .should('contain', SHORT_BIO);
  
      // Verify empty stats
      const stats = ['Total Enrollments', 'Total Participations', 'Total Assessments', 'Average Rating'];
      stats.forEach((stat, index) => {
        cy.get('.stats-container .items').eq(index)
          .should('contain', stat)
          .and('contain', '0');
      });
  
      // Verify empty participations table
      cy.get('.v-data-table').should('not.exist');
    });
  });
