(function (document, $, Coral) {
  "use strict";

  var selectors = {
    dialogContent: ".cmp-linkcollection__editor",
     multifield: "coral-multifield.custom-multifield",
  };
  var $multifield;


  $(document).on("dialog-loaded", function (event) {
    var $dialog = event.dialog;
    var $dialogContent = $dialog.find(selectors.dialogContent);
    var dialogContent =
      $dialogContent.length > 0 ? $dialogContent[0] : undefined;
    if (dialogContent) {
      $multifield = $dialogContent.find(selectors.multifield);
      handleCheckbox();
    }
  });

  function handleCheckbox() {
    if ($multifield) {
      let items = $multifield[0].items.getAll();

      if (items.length <= 0) {
        return;
      }

      Coral.commons.nextFrame(function () {

        items.forEach((item, index)  => {
            let url = items[index].content.querySelector(
                ".custom-multifield-item"
              ).value;

              let checkbox =  items[index].content.querySelector(
                ".custom-multifield-target-item"
              );
            fetch(url, { redirect: 'manual' })
              .then(response => {
                console.log(`URL: ${url}, Status Code: ${response.status}`);
                 if (response.status === 200) {
                    checkbox.checked = true;
                }else{
                    checkbox.checked = false;
                }
              })
              .catch(error => {
                checkbox.checked = false;
              });
          });
      });
    }
  }
})(document, Granite.$, Coral);
