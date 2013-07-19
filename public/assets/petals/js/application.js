!function ($) {

    $.petals = {};
    $.petals.alarms = {
        count : 0
    }

    /**
     * New monitoring alert
     *
     * @param params
     */
    $.petals.newAlert = function(params){
        this.alarms.count ++;
        var badge = $("#alert-count");
        var value = parseInt(badge.text());
        value ++;
        badge.text(value);
        badge.show();

        // show the alert div if possible (mostly available in monitoring pages)
        $("#alert-new").show();
        $("#alert-empty").hide();
        $("#alert-count").show();
    }

    /**
     * New event pushed by the server
     *
     * @param params
     */
    $.petals.newEvent = function(params){
        var badge = $("#event-count");
        var value = parseInt(badge.text());
        value ++;
        badge.text(value);
        badge.show();
    }


  $(function(){
      $.get('/monitoring/json/unread', function(result) {
          if (result > 0) {
            $("#alert-count").text(result);
            $("#alert-count").show();
          }
      })
  })

}(window.jQuery)
