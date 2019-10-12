$(document).ready(function () {
    function highlight(element) {
        $(element).addClass('highlight');
    }

    function reset() {
        $('.highlight').removeClass('highlight');
    }

    /**
     * Breadcrumb
     */
    (function () {
        const breadcrumbRow = (nodeId, kind, line, col) => `
                <li class="breadcrumb__element" data-refNodeId="${nodeId}"><a href="#${nodeId}">${kind}</a> L${line}:${col}</li>
            `;

        $('[data-kind]').click(function (e) {
            e.stopPropagation();
            reset();
            highlight(this);

            $('#breadcrumb ul')
                .empty()
                .append(
                    $(breadcrumbRow(
                        $(this).attr('id'),
                        $(this).attr('data-kind'),
                        $(this).attr('data-line'),
                        $(this).attr('data-char')
                    ))
                );

            $(this).parents('[data-kind]').each(
                function () {
                    $('#breadcrumb ul').append(
                        $(breadcrumbRow(
                            $(this).attr('id'),
                            $(this).attr('data-kind'),
                            $(this).attr('data-line'),
                            $(this).attr('data-char')
                        ))
                    );
                }
            );

            $('#breadcrumb').show();
        });

        $('#breadcrumb_close').click(function () {
            $('#breadcrumb').hide();
            reset();
        });
    })();

    /**
     * Node nav
     */
    (function () {
        $('#action_highlight').click(function (e) {
            e.preventDefault();
            $(window.location.hash).trigger('click');
        });

        $('#action_resetHighlight').click(function (e) {
            e.preventDefault();
            reset();
        });

        $('#action_nextNode').click(function (e) {
            e.preventDefault();
            window.location.hash = '#node_' + (parseInt(window.location.hash.substr(6)) + 1);
            $(window.location.hash).trigger('click');
        });

        $('#action_previousNode').click(function (e) {
            e.preventDefault();
            window.location.hash = '#node_' + (parseInt(window.location.hash.substr(6)) - 1);
            $(window.location.hash).trigger('click');
        });
    })();

    /**
     * Node refs
     */
    (function () {
        $(document)
            .on('mouseenter', '[data-refNodeId]', function () {
                reset();
                highlight($('#' + $(this).attr('data-refNodeId')));
            })
            .on('mouseleave', '[data-refNodeId]', function () {
                reset();
            });
    })();

    /**
     * Symbol table
     */
    (function () {
        $('#symbolTable_close').click(function () {
            $('#symbolTable').hide();
            reset();
        });

        function openTable(table) {
            $('#symbolTable table').hide();
            $('#symbolTable_' + table).show();
            $('#symbolTable').show();
        }

        $('[data-toggle-symbolTable]').click(function () {
            const nodeId = $(this).attr('data-toggle-symbolTable');
            window.location.hash = nodeId;
            openTable(nodeId);
        });
    })();

    /**
     * Error
     */
    (function () {
        $('.hasError')
            .mousemove(function (e) {
                $('#errors')
                    .css({
                        top: e.pageY + 10,
                        left: e.pageX + 10
                    })
                    .children().hide().parent()
                    .children('[data-error-target="' + $(this).attr('data-error-target') + '"]').show().parent()
                    .show();
            })
            .mouseleave(function () {
                $('#errors').hide();
            });
    })();

    /**
     * Folding
     */
    (function () {
        $('[data-fold-target]')
            .each(function (i, e) {
                const foldTarget = $(e).attr('data-fold-target');

                function fold(e) {
                    e.stopPropagation();
                    $('[data-fold="' + foldTarget + '"]').toggle();
                }

                $('<span class="fold-unfold fold icon" data-fold="' + foldTarget + '">+</span>')
                    .insertAfter($(e))
                    .hide()
                    .click(fold);
                $('<span class="fold-fold fold icon" data-fold="' + foldTarget + '">-</span>')
                    .insertAfter($(e))
                    .click(fold);
            });
        $('#action_fold')
            .click(function (e) {
                e.preventDefault();
                $('.icon.fold-fold').each(function (i, el) {
                    $(el).trigger('click');
                })
            });
    })();
});